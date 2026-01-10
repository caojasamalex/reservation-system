package com.djokic.service;

import com.djokic.dao.ResourcesManager;
import com.djokic.dao.UserDao;
import com.djokic.data.EditUserRequest;
import com.djokic.data.User;
import com.djokic.enumeration.RoleEnumeration;
import com.djokic.util.HmacSHA256;
import com.djokic.util.TokenData;
import com.djokic.util.TokenUtil;

import java.sql.Connection;
import java.sql.SQLException;

import static com.djokic.util.TokenUtil.authorize;

public class UserService {
    private static final UserService instance = new UserService();

    private static final HmacSHA256 hmacSHA256 = new HmacSHA256();

    private UserService(){}

    public static UserService getInstance() { return instance; }

    public String addUser(User user) throws Exception {

        if(
                user == null ||
                        user.getUsername() == null || user.getUsername().isEmpty() ||
                        user.getPassword() == null || user.getPassword().isEmpty() ||
                        user.getFullName() == null || user.getFullName().isEmpty()
        ){
            return null;
        }

        String normalizedUsername = user.getUsername().trim();
        String normalizedAndHashedPassword = hmacSHA256.hashPassword(user.getPassword().trim());
        String normalizedFullName = user.getFullName().trim();

        user.setUsername(normalizedUsername);
        user.setPassword(normalizedAndHashedPassword);
        user.setFullName(normalizedFullName);
        user.setRole(RoleEnumeration.USER);

        Connection con = null;
        try{
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);

            if(UserDao.getInstance().findByUsername(user.getUsername(), con) != null){
                throw new  Exception("Invalid username!");
            }

            int generatedId = UserDao.getInstance().insertUser(user, con);

            if(generatedId > 0){
                con.commit();

                return TokenUtil.generateToken(generatedId, user.getUsername(), user.getRole().name());
            }

            return null;
        } catch (SQLException ex) {
            if(con != null){con.rollback();}
            throw new RuntimeException(ex);
        } finally {
            ResourcesManager.closeConnection(con);
        }
    }

    public User editUser(int id, EditUserRequest editUserRequest, String authHeader) throws Exception {
        if(id <= 0){
            throw new Exception("Invalid id!");
        }

        TokenData tokenData = authorize(authHeader);

        if(tokenData.getUserId() != id){
            throw new Exception("Unauthorized!");
        }

        Connection con = null;

        try{
            con = ResourcesManager.getConnection();
            con.setAutoCommit(false);
            User user = UserDao.getInstance().findUserById(id, con);

            if(user == null) {
                throw new Exception("User not found");
            }

            if(editUserRequest == null){
                con.commit();
                return user;
            }

            if(editUserRequest.getUsername() != null && !editUserRequest.getUsername().isEmpty() && !editUserRequest.getUsername().equals(user.getUsername())){
                String normalizedUsername = editUserRequest.getUsername().trim();

                User existing = UserDao.getInstance().findByUsername(normalizedUsername, con);
                if(existing != null && existing.getUserId() != id){
                    throw new Exception("Username already taken");
                }

                user.setUsername(normalizedUsername);
            }

            if(editUserRequest.getPassword() != null && !editUserRequest.getPassword().isEmpty()){
                String normalizedAndHashedPassword = hmacSHA256.hashPassword(editUserRequest.getPassword().trim());

                user.setPassword(normalizedAndHashedPassword);
            }

            if(editUserRequest.getFullName() != null && !editUserRequest.getFullName().isEmpty() && !editUserRequest.getFullName().equals(user.getFullName())){
                String normalizedFullName = editUserRequest.getFullName().trim();

                user.setFullName(normalizedFullName);
            }

            UserDao.getInstance().updateUser(user, con);
            con.commit();

            return user;
        } catch (SQLException ex) {
            if(con != null) con.rollback();
            throw new RuntimeException(ex);
        }
    }

    public String login(User credentials) throws Exception {
        if(credentials.getUsername() == null || credentials.getUsername().isEmpty() || credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            return null;
        }

        try(Connection con = ResourcesManager.getConnection()){
            User user = UserDao.getInstance().findByUsername(credentials.getUsername(), con);

            if(user != null){
                String hashedInput = hmacSHA256.hashPassword(credentials.getPassword().trim());

                if(user.getPassword().equals(hashedInput)){
                    return TokenUtil.generateToken(
                            user.getUserId(),
                            user.getUsername(),
                            user.getRole().name()
                    );
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User getUserById(int id, String authHeader) throws Exception {
        TokenData auth = authorize(authHeader);

        boolean isOwner = (auth.getUserId() == id);
        boolean isAdmin = (auth.getRole() == RoleEnumeration.ADMIN);

        if(!isOwner && !isAdmin){
            return null;
        }

        try (Connection con = ResourcesManager.getConnection()) {
            return UserDao.getInstance().findUserById(id, con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
