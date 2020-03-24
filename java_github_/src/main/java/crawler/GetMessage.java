package crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dao.Project;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.UtilOkHttpClient2;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetMessage {
    //http客户端
    private OkHttpClient client = UtilOkHttpClient2.getUnsafeOkHttpClient();
    public String getPage(String url) throws IOException {
        //创建一个request对象，处理数据
        Request request = new Request.Builder().url(url).build();
        //创建一个会话call并执行
        Call call = client.newCall(request);

        Response response = call.execute();
        if (!response.isSuccessful()) {
            System.out.println("请求失败");
            return null;
        }
        return response.body().string();
    }

    /**
     * 得到大致符合GitHub项目的li标签
     * @param page 对应url的html
     * @return
     */
    public List<Project> getLi(String page) {
        List<Project> list = new ArrayList<>();
        //document树形结构html->树形结构
        Document document = Jsoup.parse(page);
        Elements elements = document.getElementsByTag("li");
        for(Element e: elements) {
            Elements elements1 = e.getElementsByTag("a");
            if (elements1.size() == 0) {
                continue;
            }
            Elements elements2 = elements1.get(0).getElementsByAttribute("href");
            if (elements2.size() == 0) {
                continue;
            }
            Elements elements3 = elements2.get(0).getElementsByAttributeValueMatching
                    ("href", "https://github.com/");
            if (elements3.size() == 0) {
                continue;
            }
            Element li = elements3.get(0);
            String name = li.text();
            String url = li.attr("href");
            String description = e.text();
            if ("Terms".equals(name)) {
                break;
            }
            Project project = new Project();
            project.setName(name);
            project.setUrl(url);
            project.setDescription(description);
            list.add(project);
        }
        return list;
    }

    /**
     *
     * @param repoName 形如doov-io/doov
     * @return
     */
    public String getRepoResponse(String repoName) throws IOException {
        String name = "NikyJudi";
        String password = "Nk18291068675";

        //身份认证 ，得到加密后的字符串(base64加密)
        //放入到header中
        String credentials = Credentials.basic(name, password);

        String url = "https://api.github.com/repos/" + repoName;
        Request request = new Request.Builder().url(url).
                header("Authorization", credentials).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        if (!response.isSuccessful()) {
            System.out.println("访问" + url + "失败");
            return null;
        }
        return response.body().string();
    }

    //url -> 仓库名/作者名
    //
    public String getRepoName(String url) {
        boolean f = false;
        if (url.charAt(url.length() - 1) == '/') {
            f = true;
        }
        int i1 = url.lastIndexOf("/", url.length() - 2);
        int i2 = url.lastIndexOf("/", i1 - 1);
        int index = f?url.length() - 1:url.length();

        if (i1 == -1 || i2 == -1) {
            System.out.println("url 不合法，url：" + url);
            return null;
        }
        return url.substring(i2 + 1, index);
    }

    /**
     * 得到 star，fork， issue放入到project中
     * @param jsonString 对应项目的json
     * @param project   对应项目对象
     * 使用Gson
     */
    public void getRepoInfo(String jsonString, Project project) {

        Gson gson = new GsonBuilder().create();
        //得到HashMap的一个类对象
        //{}大括号代表创建了一个匿名子类，继承自TypeToken(Gson) 再进行getType
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> hashMap = gson.fromJson(jsonString, type);

        Double star = (Double) hashMap.get("stargazers_count");
        project.setStar(star.intValue());
        Double forkCount = (Double) hashMap.get("forks_count");
        project.setFork(forkCount.intValue());
        Double issue = (Double) hashMap.get("open_issues_count");
        project.setIssue(issue.intValue());
    }
}
