package com.djokic.dao;

import com.djokic.data.Resource;
import com.djokic.enumeration.ResourceTypeEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceDao {
    private static final ResourceDao instance = new ResourceDao();

    private ResourceDao() {}

    public static ResourceDao getInstance() {
        return instance;
    }

    public List<Resource> getAllResources(Connection con) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Resource> resources = new ArrayList<>();
        try{
            ps = con.prepareStatement("SELECT * FROM resources");
            rs = ps.executeQuery();
            while(rs.next()) {
                Resource resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        ResourceTypeEnum.valueOf(rs.getString("resource_type")),
                        rs.getTime("time_from").toLocalTime(),
                        rs.getTime("time_to").toLocalTime(),
                        rs.getInt("quantity")
                );
                resources.add(resource);
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
        return resources;
    }

    public Resource getResource(int resourceId, Connection con) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Resource resource = null;

        try{
            ps = con.prepareStatement("SELECT * FROM resources WHERE resource_id = ?");
            ps.setInt(1, resourceId);
            rs = ps.executeQuery();
            if(rs.next()) {
                resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        ResourceTypeEnum.valueOf(rs.getString("resource_type")),
                        rs.getTime("time_from").toLocalTime(),
                        rs.getTime("time_to").toLocalTime(),
                        rs.getInt("quantity")
                );
            }
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }

        return resource;
    }

    public void insertResource(Resource resource, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("INSERT INTO resources (resource_name, resource_type, time_from, time_to, quantity, created_at) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, resource.getResourceName());
            ps.setString(2, resource.getResourceType().toString());
            ps.setTime(3, java.sql.Time.valueOf(resource.getTimeFrom()));
            ps.setTime(4, java.sql.Time.valueOf(resource.getTimeTo()));
            ps.setInt(5, resource.getQuantity());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(resource.getCreatedAt()));
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void updateResource(Resource resource, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("UPDATE resources SET resource_name = ?, resource_type = ?, time_from = ?, time_to = ?, quantity = ? WHERE resource_id = ?");
            ps.setString(1, resource.getResourceName());
            ps.setString(2, resource.getResourceType().toString());
            ps.setTime(3, java.sql.Time.valueOf(resource.getTimeFrom()));
            ps.setTime(4, java.sql.Time.valueOf(resource.getTimeTo()));
            ps.setInt(5, resource.getQuantity());
            ps.setInt(6, resource.getId());
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }

    public void deleteResource(int resourceId, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = con.prepareStatement("DELETE FROM resources WHERE resource_id = ?");
            ps.setInt(1, resourceId);
            ps.executeUpdate();
        } finally {
            ResourcesManager.closeResources(rs, ps);
        }
    }
}
