import gzip
import urllib
import urllib.request
import urllib.parse
import http.cookiejar
import hashlib
from bs4 import BeautifulSoup
from bs4 import element

link = []
title = []
cnt = 1
url = 'http://sse.tongji.edu.cn/InfoCenter/Lastest_Notice.aspx'

def printPut(soup):
    for k in range(0, 20):
        item = soup.find(id='GridView1_HyperLink1_' + str(k))
        print(str(k + 1) + ' ' + item.string)
        title.append(item.string)
        link.append('http://sse.tongji.edu.cn/' + item['href'][3:])
        k += 1

def ungzip(data):
    try:        # 尝试解压
        data = gzip.decompress(data)
    except:
        pass
    return data

def getContents(content):
    article = ''
    if content.string != None:
        article += content.string
        return article
    try:
        if content.contents:
            for k in range(0, len(content.contents)):
                article += getContents(content.contents[k])
    except:
        pass
    return article

def getPage(turnUrl, turnTitle):
    try:
        data = urllib.request.urlopen(turnUrl).read()
    except:
        print('Connect error!')
        exit()
    soup = BeautifulSoup(data, 'html.parser')
    content = soup.find(id='content')
    article = turnTitle + '\n'
    for k in range(0,len(content.contents)):
        article += getContents(content.contents[k])
    print(article + '\n')

def start(head={
    'Connection': 'Keep-Alive',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
    'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6',
    'User-Agent': 'Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko',
    'Accept-Encoding': 'gzip, deflate',
    'Host': 'sse.tongji.edu.cn',
}):
    cj = http.cookiejar.CookieJar()
    pro = urllib.request.HTTPCookieProcessor(cj)
    opener = urllib.request.build_opener(pro)
    header = []
    for key, value in head.items():
        elem = (key, value)
        header.append(elem)
    opener.addheaders = header
    return opener

def turnPages(opener, page, soup):
    link.clear()
    title.clear()
    state = soup.find(id='__VIEWSTATE')['value']
    generator = soup.find(id='__VIEWSTATEGENERATOR')['value']
    validation = soup.find(id='__EVENTVALIDATION')['value']
    post = {
        '__VIEWSTATE':state,
        '__VIEWSTATEGENERATOR':generator,
        '__EVENTTARGET':'GridView1$ctl23$AspNetPager1',
        '__EVENTARGUMENT':str(page),
        '__EVENTVALIDATION':validation,
        'txtTitle':'',
        'ddlPeriod':'0'
    }
    postdata = urllib.parse.urlencode(post).encode()
    data = opener.open(url, postdata).read()
    data = ungzip(data)
    soup = BeautifulSoup(data,'html.parser')
    printPut(soup)

print('Loading...')
opener = start()
try:
    data = urllib.request.urlopen(url).read()
except:
    print('Connect error!')
    exit()
soup = BeautifulSoup(data,'html.parser')
printPut(soup)
choose = 1
while choose != 'exit':
    print('当前为第%d页,输入数字选择查看信息,输入\'n\'下一页, 输入\'b\'上一页, 输入\'exit\'退出:' % cnt)
    choose = input()
    try:
        int(choose)
    except:
        if choose == 'exit':
            continue
        if choose == 'n' or choose == 'N':
            cnt += 1
            print('Loading...')
            turnPages(opener, cnt, soup)
            continue;
        if choose == 'b' or choose == 'B':
            if cnt == 1:
                print('当前为第一页!')
                continue
            cnt -= 1
            print('Loading...')
            turnPages(opener, cnt, soup)
            continue
        print('请输入数字!')
        continue
    if int(choose) >= 1 and int(choose) <= 20:
        getPage(link[int(choose) - 1], title[int(choose) - 1])
    else:
        print('超出查看范围!')