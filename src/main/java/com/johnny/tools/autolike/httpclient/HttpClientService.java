package com.johnny.tools.autolike.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("httpClientService")
@Slf4j
public class HttpClientService {
    /**
     * 编码
     */
    private final static String ENCODING = "UTF-8";
    private final static int cache = 1 * 1024;
    @Resource
    private RequestConfig requestConfig;
    @Resource
    private CloseableHttpClient httpClient;



    /**
     * get请求
     *
     * @param url 带有参数的url
     * @return
     * @throws Exception
     */
    public String get(String url) throws Exception {
        return get(url, new HashMap<String, String>());
    }

    /**
     * get请求
     *
     * @param url 带有参数的url
     * @return
     * @throws Exception
     */
    public String get(String url, Map<String, String> headerMap) throws Exception {
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        InputStream is = null;
        try {
            if (headerMap != null && headerMap.size() > 0) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    get.setHeader(key, value);
                }
            }

            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            if (is == null) {
                return null;
            }
            return IOUtils.toString(is, ENCODING);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {}
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {}
            }
        }
    }


    /**
     * post请求
     *
     * @param url        不带参数的url
     * @param formParams 参数
     * @return
     * @throws Exception
     */
    public String post(String url, List<NameValuePair> formParams) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String postFile(String url, File file,Map<String, String> headerMap) throws IOException {
        CloseableHttpResponse response = null;
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(file);
            builder.addPart("Filedata",fileBody);
            HttpEntity httpEntity = builder.build();
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null && headerMap.size() > 0) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(httpEntity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    public String post(String url, List<NameValuePair> formParams, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String post(String url, String payload, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            httpPost.setEntity(new StringEntity(payload));
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public CookieStore getCookieViaPost(String url, List<NameValuePair> formParams, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }

            HttpClientContext localContext = HttpClientContext.create();
            response = httpClient.execute(httpPost, localContext);
            return localContext.getCookieStore();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public Header[] fecthHeadersViaPost(String url, List<NameValuePair> formParams, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            response = httpClient.execute(httpPost);
            return response.getAllHeaders();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 从某个网址，通过POST方式下载文件
     *
     * @param url        网址
     * @param formParams post内容
     * @param file       保存文件
     * @return
     */
    public DownloadResult downloadViaPost(String url, List<NameValuePair> formParams, File file) {
        if (StringUtils.isBlank(url)) {
            return new DownloadResult(false, "empty download url");
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        InputStream is = null;
        FileOutputStream fos = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            DownloadResult result = new DownloadResult();
            result.setHttpCode(statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);

                byte[] buffer = new byte[cache];
                int ch = 0;
                while ((ch = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, ch);
                }
                fos.flush();
                result.setSuccess(true);
                result.setDownloadFile(file);
            } else {
                String errorMsg = EntityUtils.toString(response.getEntity());
                log.error("errorMsg = " + errorMsg);
                result.setSuccess(false);
                result.setErrorMessage(errorMsg);
            }
            return result;
        } catch (Exception e) {
            log.error("error occured while download file from " + url, e);
            return new DownloadResult(false, e.toString());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {}
            }

            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {}
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * 从某个网址，通过GET方式下载文件
     */
    public DownloadResult downloadViaGet(String url, File file, Map<String, String> headerMap) {
        if (StringUtils.isBlank(url)) {
            return new DownloadResult(false, "empty download url");
        }

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        InputStream is = null;
        FileOutputStream fos = null;
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpGet.setHeader(key, value);
                }
            }

            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(cookieStore);
            response = this.httpClient.execute(httpGet, localContext);

            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            DownloadResult result = new DownloadResult();
            result.setHttpCode(statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                is = entity.getContent();
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);

                byte[] buffer = new byte[cache];
                int ch = 0;
                while ((ch = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, ch);
                }
                fos.flush();

                result.setSuccess(true);
                result.setDownloadFile(file);
            } else {
                String errorMsg = EntityUtils.toString(response.getEntity());
                log.error("errorMsg = " + errorMsg);
                result.setSuccess(false);
                result.setErrorMessage(errorMsg);
            }
            return result;
        } catch (Exception e) {
            log.error("error occured while download file from " + url, e);
            return new DownloadResult(false, e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {}
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {}
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * 返回请求获得的流，为get请求
     *
     * @param url 带有参数的url
     * @return
     * @throws Exception
     */
    public InputStream fetchStreamViaGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (is == null) {
                return null;
            }
            return is;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 返回请求获得的流，为post请求
     *
     * @param url        不带参数的url
     * @param formParams 参数
     * @return
     * @throws IOException
     */
    public InputStream fetchStreamViaPost(String url, List<NameValuePair> formParams) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            httpPost.setEntity(uefEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (is == null) {
                return null;
            }
            return is;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String postJSON(String url, String json, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            if (StringUtils.isNotBlank(json)) {
                httpPost.setEntity(new StringEntity(json, "application/json", "UTF-8"));
            }

            httpPost.setHeader("Accept", "application/json");
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public CloseableHttpResponse postJSONAndGetStatus(String url, String json, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }
            if (StringUtils.isNotBlank(json)) {
                httpPost.setEntity(new StringEntity(json, "application/json", "UTF-8"));
                httpPost.setHeader("Accept", "application/json");
                response = httpClient.execute(httpPost);
                return response;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public String patchJSON(String url,String json,Map<String,String> headerMap) throws IOException{
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try{
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPatch.setHeader(key, value);
                }
            }
            if (StringUtils.isNotBlank(json)) {
                httpPatch.setEntity(new StringEntity(json, "application/json", "UTF-8"));
                httpPatch.setHeader("Accept", "application/json");
                response = httpClient.execute(httpPatch);
                return EntityUtils.toString(response.getEntity());
            }
        }catch (IOException e){
            throw e;
        }finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    public String postXML(String url, String xml, Map<String, String> headerMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            if (headerMap != null) {
                Iterator<String> iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    httpPost.setHeader(key, value);
                }
            }

            if (StringUtils.isNotBlank(xml)) {
                httpPost.setEntity(new StringEntity(xml, "application/xml", "UTF-8"));
                response = httpClient.execute(httpPost);

                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return null;
    }

    public String put(String url, List<NameValuePair> formParams, Map<String, String> headers) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        try {
            if (headers != null && headers.size() > 0) {
                Iterator<String> iterator = headers.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    httpPut.setHeader(key, headers.get(key));
                }
            }
            //httpPut.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            response = httpClient.execute(httpPut);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String put(String url, List<NameValuePair> formParams) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        try {
            httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPut.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            response = httpClient.execute(httpPut);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String putJSON(String url, String formParams) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        try {
            httpPut.addHeader("Content-Type", "application/json; charset=utf-8");
            httpPut.setHeader("Accept", "application/json");
            StringEntity entity = new StringEntity(formParams, "UTF-8");//解决中文乱码问题
            httpPut.setEntity(entity);
            response = httpClient.execute(httpPut);
            return EntityUtils.toString(response.getEntity());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String delete(String url) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);
        CloseableHttpResponse response = null;
        try {
            httpDelete.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpDelete.setHeader("X-Requested-With", "XMLHttpRequest");
            response = this.httpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (is == null) {
                return null;
            }
            return IOUtils.toString(is, ENCODING);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    public String deleteJSON(String url,Map<String, String> headers) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);
        CloseableHttpResponse response = null;
        try {
            httpDelete.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpDelete.setHeader("Accept", "application/json");
            if (headers != null && headers.size() > 0) {
                Iterator<String> iterator = headers.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    httpDelete.setHeader(key, headers.get(key));
                }
            }
            response = this.httpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (is == null) {
                return null;
            }
            return IOUtils.toString(is, ENCODING);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }




}
