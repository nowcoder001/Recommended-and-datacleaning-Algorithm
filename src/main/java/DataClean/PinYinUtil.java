package DataClean;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 一个工具类 可以将汉字转化为拼音 比较编辑距离
 */
public class PinYinUtil {
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    // 转换单个字符
    private static String getCharacterPinYin(char c) {
        String[] pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null)
            return null;

        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }

    // 转换单个字符,返回所有拼音
    private static List<String> getCharacterPinYins(char c) {
        String[] pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null)
            return null;

        return Arrays.asList(pinyin);
    }

    // 转换一个字符串
    public static String getStringPinYin(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for (int i = 0; i < str.length(); ++i) {
            tempPinyin = getCharacterPinYin(str.charAt(i));
            if (tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    public static boolean isPinYinSame(String text1, String text2) {
        boolean isSame = false;
        if (StringUtils.isNotBlank(text1) && StringUtils.isNotBlank(text2) && text1.length() == text2.length()) {
            int len = text1.length();
            List<String> pinYinList1 = null;
            List<String> pinYinList2 = null;
            Character char1 = null;
            Character char2 = null;
            boolean isAllSame = true;
            for (int i = 0; i < len; i++) {
                char1 = text1.charAt(i);
                char2 = text2.charAt(i);
                pinYinList1 = getCharacterPinYins(char1);
                pinYinList2 = getCharacterPinYins(char2);
                boolean isMatch = false;
                if (pinYinList1 != null && pinYinList2 != null) {
                    for (String pinyin : pinYinList1) {
                        if (pinYinList2.contains(pinyin)) {
                            isMatch = true;
                            break;
                        }
                    }
                } else if (pinYinList1 == null && pinYinList2 == null && char1.equals(char2)) {
                    isMatch = true;
                }
                if (!isMatch) {
                    isAllSame = false;
                    break;
                }
            }
            if (isAllSame) {
                isSame = true;
            }
        }
        return isSame;
    }
}
