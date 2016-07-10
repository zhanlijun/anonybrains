package com.ali.anonybrains;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GetConfigHandler {

    static Logger logger = LoggerFactory.getLogger(GetConfigHandler.class);

    private String ticket;
    private long ticketTime;

    public static String sign(String ticket, String nonceStr, long timeStamp, String url) {
        String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
                       + "&url=" + url;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return bytesToHex(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("sign NoSuchAlgorithmException error");
            return null;
        } catch (UnsupportedEncodingException e) {
            logger.error("sign UnsupportedEncodingException error");
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private void checkTicket() {
        if (ticket == null || (System.currentTimeMillis() - ticketTime) > 3600000) {
            synchronized (this) {
                if (ticket == null || (System.currentTimeMillis() - ticketTime) > 3600000) {
                    JSONObject tokenResult = HttpHelper.httpGet("https://oapi.dingtalk.com/gettoken?corpid=" + Env.CORP_ID + "&corpsecret=" + Env.CORP_SECRET);
                    if (tokenResult != null) {
                        String accessToken = tokenResult.getString("access_token");
                        if (accessToken != null) {
                            JSONObject ticketResult = HttpHelper.httpGet("https://oapi.dingtalk.com/get_jsapi_ticket?type=jsapi&access_token=" + accessToken);
                            if (ticketResult != null) {
                                ticket = ticketResult.getString("ticket");
                                if (ticket != null) {
                                    ticketTime = System.currentTimeMillis();
                                    logger.info("ticket update:" + ticket + " ticketTime:" + ticketTime);
                                } else {
                                    logger.error("get ticket null");
                                }
                            } else {
                                logger.error("get_jsapi_ticket error");
                            }
                        } else {
                            logger.error("accessToken null");
                        }
                    } else {
                        logger.error("gettoken error");
                    }
                }
            }
        }
    }

    @RequestMapping("/GetConfigHandler")
    public String handle(HttpServletRequest request) throws IOException {

        logger.info("Access getConfig ");

        try {
            Map<String, Object> result = new HashMap<String, Object>();

            logger.info("get config urlObj:"+ request.getRequestURL().toString());

            String url = request.getRequestURL().toString().replace("/getConfig", "/phone.html");

            String queryString = request.getQueryString();
            if (queryString != null) {
                logger.info("get config queryString:"+ queryString);
                url = url + "?" + URLDecoder.decode(queryString);
            }

            logger.info("get config url:"+ url);

            String nonceStr = genRandomString(7);
            long timeStamp = System.currentTimeMillis() / 1000;

            checkTicket();
            if (ticket != null) {

                logger.info("get config sign ticket:"+ ticket + " nonceStr:" + nonceStr + " timeStamp:" + timeStamp + " url:" + url);

                String signature = sign(ticket, nonceStr, timeStamp, url);
                if (signature != null) {
                    result.put("signature", signature);
                    result.put("nonceStr", nonceStr);
                    result.put("timeStamp", timeStamp);
                    result.put("corpId", Env.CORP_ID);
                    String resultString = JSON.toJSONString(result);
                    return resultString;

                } else {
                    logger.error("sign error");
                }
            } else {
                logger.error("ticket null");
            }

        } catch (Exception e) {
            logger.error("GetConfig Error", e);
        }
        return "error!";
    }

    public String genRandomString(int length) {
        String source = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int p = (int) (Math.random() * ((double) source.length()));
            sb.append(source.charAt(p));
        }
        return sb.toString();
    }

}
