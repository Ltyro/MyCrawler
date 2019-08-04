#encoding=utf-8
#上面这句话看起来是注释，但其实是有用的，指明了这个脚本的字符集编码格式
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait
import time
from pyquery import PyQuery as pq
from selenium.webdriver import ActionChains



class taobao_clawer:
	def __init__(self,url):
		#
		self.url = url
		self.options = webdriver.ChromeOptions()

		# 不加载图片,加快访问速度
		self.options.add_experimental_option("prefs", {"profile.mamaged_default_content_settings.images": 2})

		# 此步骤很重要，设置为开发者模式，防止被各大网站识别出来使用了Selenium
		self.options.add_experimental_option('excludeSwitches', ['enable-automation'])

		#self.options.add_argument('--proxy-server=127.0.0.1')

		self.browser = webdriver.Chrome(executable_path='C:\\DevelopmentKits\\python3.6\\Scripts\\chromedriver', options=self.options)
		self.wait = WebDriverWait(self.browser, 20)
		self.browser.get(url)


	def login(self):
		# 等待 密码登录选项 出现
		password_login = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, '.qrcode-login > .login-links > .forget-pwd')))
		password_login.click()

		# 等待 微博登录选项 出现
		weibo_login = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, '.weibo-login')))
		weibo_login.click()

		#获取账号输入框
		EMAIL = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#pl_login_logged > div > div:nth-child(2) > div >input')))
		EMAIL.send_keys('13588900583')

		#获取密码输入框
		PASSWD = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#pl_login_logged > div > div:nth-child(3) > div > input')))
		PASSWD.send_keys('laizaoji')

		time.sleep(2)

		#获取登陆按钮
		button = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR,'#pl_login_logged > div > div:nth-child(7) > div:nth-child(1) > a > span')))
		button.click()

		'''
		taobao_name = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'.site-nav-bd > ul.site-nav-bd-l > li#J_SiteNavLogin > div.site-nav-menu-hd > div.site-nav-user > a.site-nav-login-info-nick ')))
		# 输出淘宝昵称
		print(taobao_name.text)
		'''

	#搜索商品
	def searchinfo(self,good_name):
		#获取查询输入框
		search_value = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'.search-wrap > div > div:nth-child(2) > #J_TSearchForm > div:nth-child(2) > div:nth-child(3) > div > input')))
		search_value.send_keys(str(good_name))

		#获取查询按钮
		search_button = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'.search-wrap > .search-bd > .search-panel > #J_TSearchForm > .search-button > .btn-search')))
		search_button.click()

	#模拟向下滑动浏览
	def swipe_down(self,second):
		for i in range(int(second / 0.1)):
			js = "var q = document.documentElement.scrollTop=" + str(300 + 200 * i)
			self.browser.execute_script(js)
			time.sleep(0.1)
		#js = "var q = document.documentElement.scrollTop = 100000"
		#self.browser.execute_script(js)
		time.sleep(0.2)


	#模拟翻页操作
	def next_page(self, page_number):
		#获取下一页按钮
		next_button = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#mainsrp-pager > .m-page > .wraper > .clearfix > .form > .btn')))

		#获取页码输入框
		next_input = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#mainsrp-pager > .m-page > .wraper > .clearfix > .form > input')))

		#将当前输入框中的内容清空，并重置为page_number
		next_input.clear()
		next_input.send_keys(page_number)

		#睡眠2S
		time.sleep(2)
		next_button.click()


	#得到所有的页数
	def get_total_page(self):
		#先等待所有的商品都加载完
		goods_total = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#mainsrp-itemlist > .m-itemlist > .g-clearfix > .items')))
		#获得页数并格式化
		page_total = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#mainsrp-pager > .m-page > .wraper > .clearfix > .total')))
		result = page_total.text.strip("共 ").replace(' 页，','')
		return result

	#得到商品集
	def get_infos(self):
		list_info = []
		total_page = self.get_total_page()

		for i in range(1,int(total_page)):
			#等待页面商品数据加载完成
			goods_total = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, '#mainsrp-itemlist > .m-itemlist > .g-clearfix > .items')))

			#获取本页面源代码
			html = self.browser.page_source

			#pq模块解析网页源代码
			doc = pq(html)

			#取出淘宝商品数据
			good_items = doc('.m-itemlist .grid .items .item').items()

			#遍历该页所有的商品
			for item in good_items:
				good_title = item.find('.title').text().replace('\n', "").replace('\r', "")
				good_price = item.find('.price').text().replace('\n','').replace('\r','')
				good_sales_num = item.find('.deal-cnt').text()
				good_shop = item.find('.shop').text()

				#print(str(good_title) + str(good_price) + str(good_sales_num) +
				#str(good_shop))
				list_info.append([good_title,good_price,good_sales_num,good_shop])

			# 模拟向下滑动
			self.swipe_down(2)

			#下一页
			self.next_page(i + 1)

			time.sleep(2)

			#等待滑块验证码出现，超时时间为5s，每0.5s检查一下
			#检测是否出现滑块验证，若出现则解决
			#等待滑块加载完成
			#WebDriverWait(self.browser, 5
			#,0.5,ignored_exceptions=TimeoutError).until(EC.presence_of_element_located((By.CSS_SELECTOR,'.nc_iconfont')))
			try:
				#打印源码发现滑块源码并没有出现在当前源码中
				print(pq(self.browser.page_source))
				#尝试着切换一下frame到iframe，看一看能不能获得滑块源码
				self.browser.switch_to.frame(self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'#J_sufei > iframe'))))

				swipe_button = self.browser.find_element_by_id('nc_1_n1z')
				print(swipe_button)

				action = ActionChains(self.browser)
				action.click_and_hold(swipe_button)#perform用来执行ActionChains中存储的行为
				action.move_by_offset(580,0).perform()#移动滑块
				#action.drag_and_drop_by_offset(swipe_button, 400, 0).perform()
				action.reset_actions()

			except Exception :
				print('get swipe_button failed', Exception)

		return list_info


	#将结果写入文件中
	def write_to_csv(self,list_info):
		fw = open('result.csv','w',encoding='utf-8')
		for item in list_info:
			fw.write(','.join(item) + '\n')


if __name__ == "__main__":

	url = 'https://login.taobao.com/member/login.jhtml'
	a = taobao_clawer(url)
	a.login()
	
	#a.searchinfo('Python')
	#list_info = a.get_infos()
	#a.write_to_csv(list_info)

	time.sleep(3)
	itemUrl = 'https://item.taobao.com/item.htm?spm=a230r.1.14.1.28816146Vu4eDu&id=575115235027&ns=1&abbucket=1#detail'
	a.browser.get(itemUrl)
	ul = a.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'.J_TSaleProp.tb-img.tb-clearfix')))
	ul = a.browser.find_element_by_css_selector('.J_TSaleProp.tb-img.tb-clearfix')
	
	print(ul)
	action = ActionChains(a.browser)
	action.click(swipe_button)
	action.reset_actions()
	
	a.browser.find_element_by_css_selector('.J_TSaleProp.tb-img.tb-clearfix')