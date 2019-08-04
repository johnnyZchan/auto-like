package com.johnny.tools.autolike.services;

import com.alibaba.fastjson.JSONObject;
import com.johnny.tools.autolike.exceptions.ServiceException;
import com.johnny.tools.autolike.httpclient.HttpClientService;
import com.johnny.tools.autolike.utils.MessageUtil;
import com.johnny.tools.autolike.utils.RegExpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("autoLikeService")
@Slf4j
public class AutoLikeService {

    @Value("${autolike.dianping.url.main}")
    private String dianPingMainUrl;
    @Value("${autolike.dianping.url.follows}")
    private String dianPingFollowsUrl;
    @Value("${autolike.dianping.url.member}")
    private String dianPingMemberUrl;
    @Value("${autolike.dianping.url.review}")
    private String dianPingReviewUrl;
    @Value("${autolike.dianping.url.reviewflower}")
    private String dianPingReviewflowerUrl;

    @Resource
    HttpClientService httpClientService;

    public void autoLike(String cookie) {
        if (StringUtils.isBlank(cookie)) {
            return;
        }

        // 获取用户ID
        String userId = this.getDianPingUserId(cookie);
        if (StringUtils.isBlank(userId)) {
            String msg = MessageUtil.getMessage("dianping.can.not.get.userid");
            log.warn(msg);
            throw new ServiceException(msg);
        }
        // 获取用户的关注列表
        List<String> followList = this.getDianPingFollowsIdList(cookie, userId);
        if (followList == null || followList.isEmpty()) {
            String msg = MessageUtil.getMessage("dianping.can.not.get.follow.list");
            throw new ServiceException(msg);
        }

        // 遍历关注列表，获取每个用户的前三篇点评
        for (int i = 0; i < followList.size(); i ++) {
            String followId = followList.get(i);

            List<String> reviewList = this.getDianPingMemberReviewList(cookie, followId);
            if (reviewList == null || reviewList.isEmpty()) {
                log.warn("关注用户[" + followId + "]没有最新点评！");
                continue;
            }

            for (int j = 0; j < reviewList.size(); j ++) {
                String reviewId = reviewList.get(j);

                // 获取每一篇点评的点赞者
                List<String> reviewPraiseUserIdList = this.getDianPingReviewPraiseUserIdList(cookie, followId, reviewId);
                if (reviewPraiseUserIdList == null || reviewPraiseUserIdList.isEmpty()) {
                    log.warn("关注用户[" + followId + "]的点评[" + reviewId + "]没有点赞者！");
                    continue;
                }

                for (int k = 0; k < reviewPraiseUserIdList.size(); k ++) {
                    String reviewPraiseUserId = reviewPraiseUserIdList.get(k);
                    log.info("当前进度[关注=(" + (i + 1) + "/" + followList.size() + ")，点评=(" + (j + 1) + "/" + reviewList.size() + ")，点赞者=(" + (k + 1) + "/" + reviewPraiseUserIdList.size() + ")]");
                    this.doLike(reviewPraiseUserId, cookie);
                }
            }
        }
    }

    public void doLike(String userId, String cookie) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(cookie)) {
            return;
        }

        // 获取用户的点评列表
        Map<String, Boolean> reviewAndFlowerMap = this.getDianPingReviewAndFlowerMap(cookie, userId);
        if (reviewAndFlowerMap != null && !reviewAndFlowerMap.isEmpty()) {
            for (String reviewId : reviewAndFlowerMap.keySet()) {
                if (reviewAndFlowerMap.get(reviewId)) {
                    log.info("用户[" + userId + "]的点评[" + reviewId + "]已经点过赞了");
                    continue;
                }

                long time = new BigDecimal(Math.random() * 3000 + 1000).longValue();
                boolean result = this.like(cookie, userId, reviewId);
                log.info("用户[" + userId + "]的点评[" + reviewId + "]点赞结果[" + result + "]，睡眠" + time + "毫秒...");

                try {
                    Thread.sleep(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean like(String cookie, String userId, String reviewId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(userId) || StringUtils.isBlank(reviewId)) {
            return false;
        }

        try {
            Map<String, String> header = new HashMap<String, String>();
            header.put("Accept", "application/json, text/javascript");
            header.put("Accept-Encoding", "gzip, deflate");
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Connection", "keep-alive");
            header.put("Content-Type", "application/x-www-form-urlencoded");
            header.put("Cookie", cookie);
            header.put("Host", "www.dianping.com");
            header.put("Origin", "http://www.dianping.com");
            header.put("Referer", "http://www.dianping.com/member/" + userId);
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36");
            header.put("X-Request", "JSON");
            header.put("X-Requested-With", "XMLHttpRequest");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("do", "aa"));
            params.add(new BasicNameValuePair("i", reviewId));
            params.add(new BasicNameValuePair("t", "1"));
            params.add(new BasicNameValuePair("s", "2"));

            String response = this.httpClientService.post(dianPingReviewflowerUrl, params, header);
            if (StringUtils.isNotBlank(response)) {
                JSONObject result = JSONObject.parseObject(response);
                if (result != null && result.containsKey("code") && result.getInteger("code") == 900) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("点赞失败", e);
        }

        return false;
    }

    public String getDianPingUserId(String cookie) {
        if (StringUtils.isBlank(cookie)) {
            return null;
        }
        try {
            Map<String, String> header = new HashMap<String, String>();
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            header.put("Accept-Encoding", "gzip, deflate");
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Connection", "keep-alive");
            header.put("Host", "www.dianping.com");
            header.put("Upgrade-Insecure-Requests", "1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36");
            header.put("Cookie", cookie);
            String response = this.httpClientService.get(dianPingMainUrl, header);
            if (StringUtils.isNotBlank(response)) {
                response = RegExpUtil.replaceAllBlank(response);
                String regScript = "<scripttype=\"text/javascript\">window._DP_HeaderData=(.*?)</script>";
                String script = RegExpUtil.getTextByReg(response, regScript, 1);
                if (StringUtils.isNotBlank(script)) {
                    String regUserId = "'userId':'(.*?)'";
                    return RegExpUtil.getTextByReg(script, regUserId, 1);
                }
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
        }

        return null;
    }

    public List<String> getDianPingFollowsIdList(String cookie, String userId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(userId)) {
            return null;
        }

        try {
            String url = MessageFormat.format(dianPingFollowsUrl, userId);
            Map<String, String> header = new HashMap<String, String>();
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            header.put("Accept-Encoding", "gzip, deflate");
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Cache-Control", "max-age=0");
            header.put("Connection", "keep-alive");
            header.put("Cookie", cookie);
            header.put("Host", "www.dianping.com");
            header.put("Upgrade-Insecure-Requests", "1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36");
            String response = this.httpClientService.get(url, header);
            if (StringUtils.isNotBlank(response)) {
                response = RegExpUtil.replaceAllBlank(response);
                String regH6 = "<h6><atitle=(.*?)</a></h6>";
                List<String> h6List = RegExpUtil.getTextsByReg(response, regH6, 1);
                if (h6List != null && !h6List.isEmpty()) {
                    List<String> result = new ArrayList<String>();
                    String regFollowId = "user-id=\"(.*?)\"";
                    for (String h6 : h6List) {
                        String followUserId = RegExpUtil.getTextByReg(h6, regFollowId, 1);
                        if (StringUtils.isNotBlank(followUserId)) {
                            result.add(followUserId);
                        }
                    }

                    return result;
                }
            }
        } catch (Exception e) {
            log.error("获取用户关注列表失败", e);
        }

        return null;
    }

    public List<String> getDianPingMemberReviewList(String cookie, String userId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(userId)) {
            return null;
        }

        try {
            String response = this.getDianPingMemberReviewListContent(cookie, userId);
            if (StringUtils.isNotBlank(response)) {
                response = RegExpUtil.replaceAllBlank(response);
                String regReview = "<ahref=\"https://www.dianping.com/review/(.*?)\"";
                List<String> reviewList = RegExpUtil.getTextsByReg(response, regReview, 1);
                if (reviewList != null && !reviewList.isEmpty()) {
                    List<String> result = new ArrayList<String>();
                    reviewList.forEach(reviewId -> {
                        if (!reviewId.contains("#fn")) {
                            result.add(reviewId);
                        }
                    });

                    return result;
                }
            }
        } catch (Exception e) {
            log.error("获取用户最新点评失败", e);
        }

        return null;
    }

    public Map<String, Boolean> getDianPingReviewAndFlowerMap(String cookie, String userId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(userId)) {
            return null;
        }

        try {
            String response = this.getDianPingMemberReviewListContent(cookie, userId);
            if (StringUtils.isNotBlank(response)) {
                response = RegExpUtil.replaceAllBlank(response);
                String regReview = "<ahref=\"javascript:;\"class=\"J_floweraheart\"(.*?)data-flower=\"good\">";
                List<String> reviewList = RegExpUtil.getTextsByReg(response, regReview, 1);
                if (reviewList != null && !reviewList.isEmpty()) {
                    Map<String, Boolean> result = new HashMap<String, Boolean>();
                    String regDataSend = "data-send=\"(.*?)\"";
                    String regDataId = "data-id=\"(.*?)\"";
                    for (String review : reviewList) {
                        String dataSend = RegExpUtil.getTextByReg(review, regDataSend, 1);
                        String dataId = RegExpUtil.getTextByReg(review, regDataId, 1);
                        if (StringUtils.isNotBlank(dataSend) && StringUtils.isNotBlank(dataId)) {
                            result.put(dataId, Boolean.parseBoolean(dataSend));
                        }
                    }

                    return result;
                }
            }
        } catch (Exception e) {
            log.error("获取用户最新点评和点赞情况Map失败", e);
        }

        return null;
    }

    public String getDianPingMemberReviewListContent(String cookie, String userId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(userId)) {
            return null;
        }

        try {
            String url = MessageFormat.format(dianPingMemberUrl, userId);
            Map<String, String> header = new HashMap<String, String>();
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            header.put("Accept-Encoding", "gzip, deflate");
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Cache-Control", "max-age=0");
            header.put("Connection", "keep-alive");
            header.put("Cookie", cookie);
            header.put("Host", "www.dianping.com");
            header.put("Upgrade-Insecure-Requests", "1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36");
            String response = this.httpClientService.get(url, header);
            return response;
        } catch (Exception e) {
            log.error("获取用户最新点评HTML失败", e);
        }

        return null;
    }

    public List<String> getDianPingReviewPraiseUserIdList(String cookie, String userId, String reviewId) {
        if (StringUtils.isBlank(cookie) || StringUtils.isBlank(reviewId) || StringUtils.isBlank(userId)) {
            return null;
        }

        try {
            String url = MessageFormat.format(dianPingReviewUrl, reviewId);
            Map<String, String> header = new HashMap<String, String>();
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            header.put("Accept-Encoding", "gzip, deflate, br");
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Connection", "keep-alive");
            header.put("Cookie", cookie);
            header.put("Host", "www.dianping.com");
            header.put("Referer", "http://www.dianping.com/member/" + userId);
            header.put("Upgrade-Insecure-Requests", "1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36");
            String response = this.httpClientService.get(url, header);
            if (StringUtils.isNotBlank(response)) {
                response = RegExpUtil.replaceAllBlank(response);
                String regPraiseUserId = "<lidata-userid=\"(.*?)\">";
                List<String> praiseUserIdList = RegExpUtil.getTextsByReg(response, regPraiseUserId, 1);
                return praiseUserIdList;
            }
        } catch (Exception e) {
            log.error("获取点评点赞人列表失败", e);
        }

        return null;
    }
}
