package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {
    public void save(Project project) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "insert into project values(?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, project.getName());
            statement.setString(2, project.getUrl());
            statement.setString(3, project.getDescription());
            statement.setInt(4, project.getStar());
            statement.setInt(5, project.getFork());
            statement.setInt(6, project.getIssue());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            statement.setString(7, dateFormat.format(System.currentTimeMillis()));

            int ret = statement.executeUpdate();
            if (ret != 1) {
                System.out.println("数据库插入失败");
                return;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, null );
        }

    }

    public List<Project> selectByDate(String date) {
        List<Project> projects = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.prepareStatement
                    ("select * from project where date = ? order by star desc; ");
            statement.setString(1, date);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Project project = new Project();
                project.setName(resultSet.getString("name"));
                project.setUrl(resultSet.getString("url"));
                project.setDescription(resultSet.getString("description"));
                project.setStar(resultSet.getInt("star"));
                project.setFork(resultSet.getInt("fork"));
                project.setIssue(resultSet.getInt("issue"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return projects;
    }
}
