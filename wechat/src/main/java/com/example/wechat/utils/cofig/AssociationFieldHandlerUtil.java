package com.example.wechat.utils.cofig;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 浙联社字段处理工具类
 *
 * 
 * @Date 2019年11月13日 下午3:56:31
 */
@Slf4j
public class AssociationFieldHandlerUtil {

    /**
     * 百分号
     */
    public static final String PERCENT = "%";

    /**
     * 文本 key
     */
    public static final String TEXT_KEY = "T";

    /**
     * 数值key
     */
    public static final String NUM_KEY = "N";


    //*******************量******************
    /**
     * 数值10
     */
    public static final BigDecimal TEN = BigDecimal.TEN;

    public static final String TENTHOUSAND = "万";

    public static final String TENTHOUSAND_EN = "W|w";

    public static final String HUNDREDMILLION = "亿";

    public static final String HUNDREDMILLION_EN = "E|e|个亿?";

    public static final BigDecimal E_MULTI = new BigDecimal("10000");

    public static final String PLUS = "+";

    public static final String PLUS_REG = "\\+";

    public static final String LESS_THAN_CN = "以内";

    public static final String MIDDLE_LINE = "-";

    //*******************量******************

    //*******************价格******************

    public static final String PLUSQ = "加权";

    public static final String PLUSD = "加点";

    public static final String MIND = "减点";

    public static final String BP = "BP";

    public static final String DR = "DR";

    private static final Set<String> priceSet = new HashSet<String>() {{
        add("加权");
        add("加点");
        add("减点");
    }};

    //*******************价格******************

    //*******************期限******************
    //隔夜
    private static final String NEXT_NINGH = "隔夜";

    //1天
    private static final String ONE_DAY = "1D";
    //天简称
    public static final String DAY = "D";

    //月简称
    private static final String MONTH = "M";

    private static final String YEAR = "Y";

    public static final String DAY_CN = "天";

    public static final String MONTH_CN = "月";

    public static final String YEAR_CN = "年";

    public static final String OR = " +OR +";

    public static final String OR_CN = "或";

    public static final String WAVE = "~";

    public static final String COMMA = ",";

    public static final String MONTH_INNER = "月内";

    public static final BigDecimal YEAR_MULTI = new BigDecimal("365");

    public static final BigDecimal MONTH_MULTI = new BigDecimal("30");

    public static final BigDecimal DAY_MULTI = new BigDecimal("1");

    //分隔
    private static final String SEPERATE = "-";

    public static List<String> deadLineSet = Lists.newArrayList("隔夜","7D","14D","21D","1M","3M","6M");


    /**
     * **点还款
     */
    private static final String fundHk1 = "还款";

    /**
     * 时
     */
    private static final String hourZh = "点";

    /**
     * 时
     */
    private static final String hourEn = ":";

    /**
     * am-zh
     */
    private static final String hourAmZh = "上午";

    /**
     * pm-zh
     */
    private static final String hourPmZh = "下午";

    /**
     * 标签连接符
     */
    public static final String tagJoinLine = "/";


    public static final String YUAN = "元";

    /**
     * Description :量字段处理
     *
     * @param amountText
     * @return
     * 
     * @Date 2019年11月13日 下午10:17:10
     */
    public static TextNumberResult amountHandler(String amountText) {
        TextNumberResult result = new TextNumberResult();
        try {
            amountText = amountText.replaceAll(" ", "");
            //是数字
            if (isNumber(amountText)) {
                BigDecimal amount = new BigDecimal(amountText);
                if (amount.compareTo(TEN) > 0) {
                    return new TextNumberResult(amount + TENTHOUSAND, amount);
                } else {
                    return new TextNumberResult(amountText + HUNDREDMILLION, multiplyAndHalfUpScale(amount, E_MULTI, 4));
                }
            }
            //w -> 万
            amountText = amountText.replaceAll(TENTHOUSAND_EN, TENTHOUSAND)
                    // e | 个 -> 亿
                    .replaceAll(HUNDREDMILLION_EN, HUNDREDMILLION)
                    // 元 -> ''
                    .replaceAll(YUAN, "")
            ;
            if (!amountText.contains(PLUS)) {
                // 没有 +
                result.setText(amountText);
                amountText = amountText.replaceAll(LESS_THAN_CN, "");
                if (amountText.indexOf(MIDDLE_LINE) > 0) {
                    String number = getStartNumber(amountText);
                    if (amountText.endsWith(TENTHOUSAND)) {
                        return new TextNumberResult(amountText, number);
                    } else if (amountText.endsWith(HUNDREDMILLION)) {
                        return new TextNumberResult(amountText, timesTenThousand(number));
                    } else if (new BigDecimal(number).compareTo(TEN) > 0) {
                        return new TextNumberResult(amountText + TENTHOUSAND, number);
                    } else {
                        return new TextNumberResult(amountText + HUNDREDMILLION, timesTenThousand(number));
                    }
                }
                if (amountText.contains(HUNDREDMILLION)) {
                    result.setNumber(timesTenThousand(amountText.replaceAll(HUNDREDMILLION, "")));
                } else if (amountText.contains(TENTHOUSAND)) {
                    result.setNumberString(getStartNumber(amountText));
                }
            } else {
                // 有 +
                String[] strs = amountText.split(PLUS_REG);
                StringBuilder sb = new StringBuilder();
                boolean hasE = false;
                boolean hasW = false;
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (int i = 0; i < strs.length; i++) {
                    if (isNumber(strs[i])) {
                        BigDecimal amount = new BigDecimal(strs[i]);
                        if (amount.compareTo(TEN) > 0) {
                            sb.append(strs[i] + TENTHOUSAND);
                            totalAmount = totalAmount.add(amount);
                            hasW = true;
                        } else {
                            sb.append(strs[i] + HUNDREDMILLION);
                            totalAmount = totalAmount.add(amount.multiply(E_MULTI));
                            hasE = true;
                        }
                    } else {
                        sb.append(strs[i]);
                        if (strs[i].indexOf(HUNDREDMILLION) >= 0) {
                            BigDecimal amount = new BigDecimal(strs[i].replaceAll(HUNDREDMILLION, ""));
                            totalAmount = totalAmount.add(amount.multiply(E_MULTI));
                            hasE = true;
                        } else if (strs[i].indexOf(TENTHOUSAND) >= 0) {
                            BigDecimal amount = new BigDecimal(strs[i].replaceAll(TENTHOUSAND, ""));
                            totalAmount = totalAmount.add(amount);
                            hasW = true;
                        }
                    }
                    if (i != strs.length - 1) {
                        sb.append(PLUS);
                    }
                }
                //只有亿，没有万 || 只有万，没有亿 || 单位一致
                if (hasE && !hasW) {
                    result.setText(sb.toString().replaceAll(HUNDREDMILLION, "") + HUNDREDMILLION);
                } else if (hasW && !hasE) {
                    result.setText(sb.toString().replaceAll(TENTHOUSAND, "") + TENTHOUSAND);
                } else {
                    result.setText(sb.toString());
                }
                result.setNumber(totalAmount.setScale(4, RoundingMode.HALF_UP));
            }
            if (result.getText() == null) {
                result.setText(amountText);
            }
        } catch (Exception e) {
            log.error("amountHandler error ", e);
            result.setText(amountText);
            result.setNumber(null);
        }
        return result;
    }

    /**
     * Description :是否为数字或者小数
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月13日 下午10:23:42
     */
    public static boolean isNumber(String str) {
        String reg = "\\d+(\\.\\d+)?";
        return str.matches(reg);
    }


    private static final Pattern PATTERN_ENDS_WITH_PLUS_AND_NUMBER = Pattern.compile("\\+\\d+BP$");

    /**
     * Description :是否为+数字+BP结尾
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月13日 下午10:23:42
     */
    public static boolean isEndWithPlusNumber(String str) {
        Matcher matcher = PATTERN_ENDS_WITH_PLUS_AND_NUMBER.matcher(str);
        return matcher.find();
    }


    private static final Pattern PATTERN_STARTS_WITH_NUMBER_AND_DM = Pattern.compile("^(\\d+[D|M|Y]?)");

    /**
     * Description :获取开头的数字，并可能带DM结尾
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月13日 下午10:23:42
     */
    public static String getStartNumberAndDM(String str) {
        Matcher matcher = PATTERN_STARTS_WITH_NUMBER_AND_DM.matcher(str);
        return matchOrNull(matcher);
    }

    public static final Pattern PATTERN_STARTS_WITH_NUMBER = Pattern.compile("^\\d?\\.?\\d+");

    public static String getStartNumber(String str) {
        return matchOrNull(PATTERN_STARTS_WITH_NUMBER.matcher(str));
    }


    public static String matchOrNull(Matcher matcher) {
        return matcher.find() ? matcher.group() : null;
    }

    private static final Pattern PATTERN_IS_DATE_OR_MONTH = Pattern.compile("^\\d+[D|M]$");

    /**
     * Description :是否是deadLine精确格式
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月19日 下午4:24:05
     */
    public static boolean isDeadLineFormat(String str) {
        if (NEXT_NINGH.equals(str)) {
            return true;
        }
        Matcher matcher = PATTERN_IS_DATE_OR_MONTH.matcher(str);
        return matcher.find();
    }

    /**
     * Description :是否是amount精确格式
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月19日 下午4:32:28
     */
    public static boolean isAmountFormat(String str) {
        Pattern pattern = Pattern.compile("^(\\d+([万]|[亿])|\\d+\\.\\d+([万]|[亿]))$");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Description :是否是价格精确格式
     *
     * @param str
     * @return
     * 
     * @Date 2019年11月19日 下午4:32:32
     */
    public static boolean isPriceFormat(String str) {
        if (priceSet.contains(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("^(\\d+%|\\d+\\.\\d+%)$");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }


    /**
     * Description :期限数值处理
     *
     * @param deadLineText
     * @return
     */
    public static Map<String, String> deadLineHandler(String deadLineText) {
        Map<String, String> result = new HashMap<>();
        try {
            //月内
            if (deadLineText.indexOf(MONTH_INNER) >= 0) {
                deadLineText = deadLineText.trim().toUpperCase().replaceAll(WAVE, SEPERATE).replaceAll(OR, OR_CN).replaceAll(" +", COMMA);
            } else {
                deadLineText = deadLineText.trim().toUpperCase().replaceAll(WAVE, SEPERATE)
                        .replaceAll(DAY_CN, DAY).replaceAll(MONTH_CN, MONTH).replaceAll(OR, OR_CN).replaceAll(" +", COMMA);
            }
            //纯数字
            if (isNumber(deadLineText)) {
                result.put(TEXT_KEY, deadLineText + DAY);
                result.put(NUM_KEY, deadLineText);
                if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
                    Integer.parseInt(result.get(NUM_KEY));
                }
                return result;
            }
            //隔夜
            if (deadLineText.indexOf(NEXT_NINGH) >= 0) {
                if (deadLineText.equals(NEXT_NINGH)) {
                    result.put(TEXT_KEY, deadLineText);
                    result.put(NUM_KEY, DAY_MULTI.toString());
                    if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
                        Integer.parseInt(result.get(NUM_KEY));
                    }
                    return result;
                } else {
                    deadLineText = deadLineText.replaceAll(NEXT_NINGH, "1D");
                }
            }
            //1D
            if (deadLineText.equals(ONE_DAY)) {
                result.put(TEXT_KEY, NEXT_NINGH);
                result.put(NUM_KEY, DAY_MULTI.toString());
                if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
                    Integer.parseInt(result.get(NUM_KEY));
                }
                return result;
            }
            //包含-，但不含,
            if (deadLineText.contains(SEPERATE) && !deadLineText.contains(COMMA)) {
                return deadLineWithSeparateHandler(deadLineText);
            }
            //包含,
            if (deadLineText.contains(COMMA)) {
                List<String> stringList = Arrays.stream(deadLineText.split(COMMA)).distinct().collect(Collectors.toList());
                StringBuffer sb = new StringBuffer();
                String num = null;
                for (int i = 0; i < stringList.size(); i++) {
                    String current = stringList.get(i);
                    String currentText;
                    if (current.contains(SEPERATE)) {
                        Map<String, String> map = deadLineWithSeparateHandler(current);
                        currentText = map.get(TEXT_KEY);
                        if (i == 0) {
                            num = map.get(NUM_KEY);
                        }
                    } else {
                        currentText = current;
                        if (i == 0) {
                            num = deadLineNumHandler(current, false, false);
                        }
                    }
                    if (isEndsWithNumber(currentText)) {
                        currentText = currentText + "D";
                    }
                    sb.append(currentText);
                    if (i != stringList.size() - 1) {
                        sb.append(COMMA);
                    }
                }
                result.put(TEXT_KEY, sb.toString());
                if (StringUtils.isNotBlank(num)) {
                    result.put(NUM_KEY, num);
                }
                if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
                    Integer.parseInt(result.get(NUM_KEY));
                }
                return result;
            }
            String startText = getStartNumberAndDM(deadLineText);
            if (startText != null && deadLineText.indexOf(MONTH_INNER) < 0) {
                result.put(TEXT_KEY, deadLineText);
                result.put(NUM_KEY, deadLineNumHandler(startText, false, false));
            } else {
                result.put(TEXT_KEY, deadLineText);
            }
            if (!deadLineSet.contains(result.get(TEXT_KEY)) && isDeadLineFormat(result.get(TEXT_KEY))) {
                result.put(TEXT_KEY, result.get(NUM_KEY) + DAY);
            }
            if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
                Integer.parseInt(result.get(NUM_KEY));
            }
            return result;
        } catch (Exception e) {
            log.error("deadLineHandler error ", e);
            result.put(TEXT_KEY, deadLineText);
            result.put(NUM_KEY, null);
        }
        return result;
    }

    private static final Pattern PATTERN_ENDS_WITH_NUMBER = Pattern.compile("\\d$");

    private static boolean isEndsWithNumber(String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        return PATTERN_ENDS_WITH_NUMBER.matcher(s).find();
    }

    /**
     * Description :处理包含分隔符
     *
     * @param deadLineText
     * @return
     * 
     * @Date 2019年11月14日 下午5:01:52
     */
    public static Map<String, String> deadLineWithSeparateHandler(String deadLineText) {
        Map<String, String> result = new HashMap<String, String>();
        String[] strs = deadLineText.split(SEPERATE);
        StringBuilder sb = new StringBuilder();
        boolean hasD = false;
        boolean hasM = false;
        for (int i = 0; i < strs.length; i++) {
            if (isNumber(strs[i])) {
                sb.append(strs[i]);
            } else {
                sb.append(strs[i]);
                if (strs[i].contains(DAY)) {
                    hasD = true;
                } else if (strs[i].contains(MONTH)) {
                    hasM = true;
                }
            }
            if (i != strs.length - 1) {
                sb.append(SEPERATE);
            }
        }
        //只有D，没有M || 只有M，没有D || 单位不一致
        if (hasD && !hasM) {
            result.put(TEXT_KEY, sb.toString().replaceAll(DAY, "") + DAY);
        } else if (hasM && !hasD) {
            result.put(TEXT_KEY, sb.toString().replaceAll(MONTH, "") + MONTH);
        } else {
            result.put(TEXT_KEY, sb.toString());
        }
        String startNumberAndDM = getStartNumberAndDM(result.get(TEXT_KEY));
        String startNum = deadLineNumHandler(startNumberAndDM, hasM, hasD);
        if (startNum != null) {
            result.put(NUM_KEY, startNum);
        }
        if (StringUtils.isNotBlank(result.get(NUM_KEY))) {
            log.debug("parse result: " + Integer.parseInt(result.get(NUM_KEY)));
        }
        if (isEndsWithNumber(result.get(TEXT_KEY))) {
            result.put(TEXT_KEY, result.get(TEXT_KEY) + "D");
        }
        return result;
    }

    /**
     * Description :处理数值
     *
     * @param startText
     * @return
     * 
     * @Date 2019年11月14日 下午5:22:19
     */
    public static String deadLineNumHandler(String startText, boolean hasM, boolean hasD) {
        String result = null;
        if (StringUtils.isNotBlank(startText)) {
            if (startText.endsWith(DAY)) {
                result = startText.replace(DAY, "");
            } else if (startText.endsWith(MONTH)) {
                result = new BigDecimal(startText.replace(MONTH, ""))
                        .multiply(MONTH_MULTI).setScale(0).toString();
            }else if(startText.endsWith(YEAR) || startText.endsWith(YEAR.toLowerCase())){
                result = new BigDecimal(startText.replace(YEAR, ""))
                        .multiply(YEAR_MULTI).setScale(0).toString();
            } else {
                if (hasM && !hasD) {
                    result = new BigDecimal(startText)
                            .multiply(MONTH_MULTI).setScale(0).toString();
                } else {
                    result = startText;
                }
            }
        }
        return result;
    }



    /**
     * Description :获取开头的时间
     *
     * @param str
     * @return
     * 
     * @Date 2020年1月15日 上午11:18:34
     */
    public static String getTime(String str) {
        Pattern pattern = Pattern.compile("^(\\d+:\\d+|\\d+)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TextNumberResult {
        String text;
        BigDecimal number;

        public TextNumberResult(String text) {
            this.text = text;
        }

        public TextNumberResult(String text, String number) {
            this.text = text;
            this.setNumberString(number);
        }

        public void setNumberString(String numberString) {
            if (StringUtils.isBlank(numberString)) {
                return;
            }
            try {
                this.number = new BigDecimal(numberString);
            } catch (RuntimeException e) {
                this.number = null;
            }
        }
    }


    public static BigDecimal timesTenThousand(String origin) {
        return multiplyAndHalfUpScale(new BigDecimal(origin), E_MULTI, 4);
    }

    private static BigDecimal multiplyAndHalfUpScale(BigDecimal input, BigDecimal multiply, int scale) {
        return input.multiply(multiply).setScale(scale, RoundingMode.HALF_UP);
    }

}
