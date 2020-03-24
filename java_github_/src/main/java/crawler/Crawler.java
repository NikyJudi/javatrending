package crawler;

import dao.Project;
import dao.ProjectDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Crawler {

    private static String url = "https://github.com/akullpp/awesome-java/blob/master/README.md";

    public static void main(String[] args) throws IOException, SQLException {
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        long startTime = System.currentTimeMillis();

        GetMessage message = new GetMessage();

        String page = message.getPage(url);
        long endTime = System.currentTimeMillis();

        System.out.println("解析网页时间：" + (endTime - startTime) + "ms");
        List<Project> projects = message.getLi(page);
        System.out.println("得到li标签时间：" + (System.currentTimeMillis() - endTime) + "ms");
        endTime = System.currentTimeMillis();
        List<Future<?>> futureList = new ArrayList<>();
        for (Project project : projects) {
            Future<?> future = executorService.submit(new CrawlerTask(project, message));
            futureList.add(future);
        }

        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        System.out.println("线程池处理项目时间：" + (System.currentTimeMillis() - endTime) + "ms");
        endTime = System.currentTimeMillis();

        ProjectDao projectDao = new ProjectDao();
        boolean f = true;
        for (Project project : projects) {
            if (f){
                f = false;
                continue;
            }
            projectDao.save(project);
        }
        System.out.println("保存到数据库中时间：" + (System.currentTimeMillis() - endTime) + "ms");
        System.out.println("整体时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }
}

class CrawlerTask implements Runnable {
    private Project project;
    private GetMessage message;

    public CrawlerTask(Project project, GetMessage message) {
        this.project = project;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            String repoName = message.getRepoName(project.getUrl());
            String jsonString = message.getRepoResponse(repoName);
            message.getRepoInfo(jsonString, project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
