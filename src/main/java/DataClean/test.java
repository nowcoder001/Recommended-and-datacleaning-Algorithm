package DataClean;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.regex.Pattern;

import static java.lang.Integer.min;

public class test {

    public static void main(String[] args) {
        String a = "aaa#bbb#cccc";

        System.out.println(filtration(a));
    }
    public static String filtration(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？']";
        str = Pattern.compile(regEx).matcher(str).replaceAll("").trim();
        return str;
    }
}
