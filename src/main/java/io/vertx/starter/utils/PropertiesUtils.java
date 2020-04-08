package io.vertx.starter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author Ginny Hu
 * @create 2020/4/8 10:57
 */
public class PropertiesUtils {

  private String propertiesName = "";

  public PropertiesUtils(String fileName) {
    this.propertiesName = fileName;
  }
  /**
   * 读取配置文件
   * @return Properties
   */
  public Properties readProperties(){
    InputStream queriesInputStream = getClass().getResourceAsStream(propertiesName);
    Properties queriesProps = new Properties();
    try {
      queriesProps.load(queriesInputStream);
      return queriesProps;
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      try {
        queriesInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return queriesProps;
  }
}
