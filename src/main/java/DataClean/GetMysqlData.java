package DataClean;

import Abstract.TextRankKeyword;
import Abstract.TextRankSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public  class GetMysqlData {

    Connection conn = null;
    Statement stmt = null;
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://rm-bp150y3hw72w7t85a5o.mysql.rds.aliyuncs.com:3306/blogstorm?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=CST";
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "blogstorm";
    static final String PASS = "Mountain2";
    public GetMysqlData()
    {
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    }

    public List<ArticleBean> getArticle()
    {
        List<ArticleBean> list = new ArrayList<>();
        try{
            String sql;
            sql = "SELECT id,title, content,tags FROM article_copy1";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id  = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String tags = rs.getString("tags");
                ArticleBean articleBean = new ArticleBean(id,title,content,tags);
                list.add(articleBean);

            }
            // 完成后关闭
            rs.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
        return list;
    }
    public List<Tag> getTag()
    {
        List<Tag> list = new ArrayList<>();
        try{
            String sql;
            sql = "SELECT id,name,description FROM tag";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id  = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Tag tag = new Tag(id,name,description);
                list.add(tag);
            }
            // 完成后关闭
            rs.close();

        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
        return list;
    }
    public List<Classify> getClassify()
    {
        List<Classify> list = new ArrayList<>();
        try{
            String sql;
            sql = "SELECT id,name,description FROM classify";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id  = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Classify classify = new Classify(id,name,description);
                list.add(classify);
            }
            // 完成后关闭
            rs.close();

        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
        return list;
    }
    public void UpdateArticle(int id,String tags,String classifys,String clean_content,String keyword,String summary)
    {
        try {

                         String sql = "Update article_copy1 set tags=?,classifys=?,clean_content=?,keyword=?,summary = ? where id=?";
                         // 预处理sql语句
                         PreparedStatement presta = conn.prepareStatement(sql);
                         // 设置sql语句中的values值
                         presta.setString(1, tags);
                         presta.setString(2, classifys);
                         presta.setString(3, clean_content);
                        presta.setString(4, keyword);
                 presta.setString(5, summary);
                    presta.setInt(6, id);
                         // 执行SQL语句，实现数据添加
                         presta.execute();
                     } catch (SQLException e) {
                         e.printStackTrace();
                     }
    }
}
