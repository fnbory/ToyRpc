package common;

import java.net.URL;

/**
 * @Author: fnbory
 * @Date: 2019/9/29 20:26
 */
public class ExtentionLoader {
    private static ExtentionLoader extentionLoader=new ExtentionLoader();
    public static ExtentionLoader getInstance(){
        return extentionLoader;
    }

    public void loadResurce() {
        URL url=this.getClass().getResource("/ewf.xml");
        System.out.println(url);
    }

    public static void main(String[] args) {
        System.out.println(extentionLoader.getClass().getClassLoader());
        extentionLoader.loadResurce();
    }
}
