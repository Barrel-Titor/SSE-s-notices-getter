# SSE-s-notices-getter
利用python爬虫获取同济大学软件学院官网通知

## 环境及python库要求
### PYTHON版本
  python 3.4
### 库
 [gzip](http://www.gzip.org/)
  用于解压网页信息</br>
  
      pip install gzip
  
 [urllib](https://docs.python.org/2/library/urllib.html)
  用于获取网页源码</br>
  
      pip install urllib
  
 [bs4](http://www.crummy.com/software/BeautifulSoup/bs4/doc/)
  强大的网页源码处理库</br>
  
      pip install bs4
  
## 功能介绍
 将[同济大学软件学院官网学院通知](http://www.crummy.com/software/BeautifulSoup/bs4/doc/)前20条通知抓取下来，可选择查看，也可选择翻页获许往后的20条
 
## 操作说明
    '1-20' —————————— 查看对应编号通知
    'n'    —————————— 向后翻页
    'b'    —————————— 向前翻页
    'exit' —————————— 退出

## 演示
 ![](https://github.com/Hjyheart/SSE-s-notices-getter/blob/master/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202016-03-07%20%E4%B8%8B%E5%8D%886.13.15.png)
