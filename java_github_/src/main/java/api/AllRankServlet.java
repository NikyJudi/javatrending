package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Project;
import dao.ProjectDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AllRankServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");

        //http://39.104.110.111:8080/rank/allRank?date=20200323
        String date = req.getParameter("date");
        if (date == null || date.equals("")) {
            resp.setStatus(404);
            resp.getWriter().write("error date");
            return;
        }

        //查找数据
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectByDate(date);

        //将数据转化为json
        Gson gson = new GsonBuilder().create();
        String respJson = gson.toJson(projects);
        resp.getWriter().write(respJson);
    }
}
