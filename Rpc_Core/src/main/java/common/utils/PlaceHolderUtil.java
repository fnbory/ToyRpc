package common.utils;

import common.enumeration.ERROR_ENUM;
import common.exception.Rpcexception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 17:24
 */
public class PlaceHolderUtil {

    private static final Pattern REGEX = Pattern.compile("\\{(.*?)}");

    public static String replace(String message, Object... args) {
        Matcher matcher = REGEX.matcher(message);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (matcher.find()) {
            // placeHolder 格式为scope.x.y.z
            // scope值为requestScope,sessionScope,applicationScope
            String placeHolder = matcher.group(1);
            if (index >= args.length) {
                throw new Rpcexception(ERROR_ENUM.TEMPLATE_REPLACEMENT_ERROR,"出错消息模板替换出错,placeHolder:{}", placeHolder);
            }
            Object value = args[index++];
            // 如果解析得到的值为null，则将占位符去掉；否则将占位符替换为值
            if (value == null) {
                matcher.appendReplacement(sb, "");
            } else {
                //把group(1)得到的数据，替换为value
                matcher.appendReplacement(sb, value.toString());
            }
        }
        // 将源文件后续部分添加至尾部
        matcher.appendTail(sb);
        return sb.toString();
    }
}
