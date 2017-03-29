# frame-dao
这是一个Java ORM框架，底层实现是基于Spring JDBC。

很多时候大家都会选择一些成熟的ORM框架，比如MyBatis，Hibernate等。这些框架确实功能强大，但事实上我们在做互联网应用时，我们更需要能够精细控制sql语句，比如某些业务场景我们只取某张表的部分字段，还有一些复杂查询等。而且这些ORM框架配置复杂，需要写很多mapper映射文档。

当我们在使用Spring JDBC时，发现功能强大，而且不需要配置文件，没有配置文件，当然使用起来也要多写很多代码。那现在这个dao层框架既要保留JDBC的灵活性、又要有ORM映射能力。

# 功能

### 1. 查询
* 主键查询
	- 支持只查询部分字段 
		> <E> E getById(Class<E> cls, Serializable id, String... propertyNames);
	- 支持查询时锁定该条记录 
		> <E> E getById(Class<E> cls, boolean isLock, Serializable id, String... propertyNames)
* 根据对象进行查询
* 根据SQL进行查询，返回指定对象
* 根据某个属性进行查询
* 分页查询
### 保存对象
* 新增：save
* 更新：update
### 删除对象
* 根据主键删除
* 根据对象进行删除

### 主键生成方式
* 自增长，支持的数据库有MySQL、SQLServer，DB2
* 自定义
	- UUID IdCreatorUuid
	- Twitter的雪花算法 IdCreatorSnowflake
	- 还可以扩展，实现接口：IdCreator

# 性能
性能非常接近原生JDBC，由于所有的业务对象Bean，都在第一次使用时进行了反射关系的缓存，所以在后续操作中都非常快。


# 使用
### maven 引入

	<dependency>
		<groupId>com.hu.wxky</groupId>
		<artifactId>frame-dao</artifactId>
		<version>1.0</version>
	</dependency>
	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-jdbc</artifactId>
		<version>4.3.5.RELEASE</version>
	</dependency>
	<dependency>
		<groupId>org.slf4j</groupId>
		<artifactId>slf4j-api</artifactId>
		<version>1.6.4</version>
	</dependency>
	<dependency>
		<groupId>org.slf4j</groupId>
		<artifactId>slf4j-log4j12</artifactId>
		<version>1.6.4</version>
	</dependency>
    


### 例子
	
	public class UserInfo extends BaseBean {

		private static final long serialVersionUID = -2190047122624232714L;
		@PrimaryKey(generateKey=GenerateKey.ASSIGNED, refObj="idSnowflake")
		private Long id;
		private String username;
		private String loginAccount;
		private String loginPwd;
		private Date created;
		private Integer status;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getLoginAccount() {
			return loginAccount;
		}
	
		public void setLoginAccount(String loginAccount) {
			this.loginAccount = loginAccount;
		}
		public String getLoginPwd() {
			return loginPwd;
		}
		public void setLoginPwd(String loginPwd) {
			this.loginPwd = loginPwd;
		}
		public Date getCreated() {
			return created;
		}
		public void setCreated(Date created) {
			this.created = created;
		}
		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
	}
	public interface IUserInfoDao extends IBaseDao {

		public UserInfo getByAccount(String account, String...attrs);
		/**
		 * 分页查询
		 * @param status
		 * @param page 分页信息
		 */
		public void getPage(int status, PageVo<UserInfo> page);
	}
	
	@Repository
	public class UserInfoDao extends BaseDao implements IUserInfoDao {
	
		@Override
		public UserInfo getByAccount(String account, String... attrs) {
			StringBuilder sb = attrToSql(UserInfo.class, attrs);
			sb.append(WHERE).append("login_account=?");
			return queryOne(UserInfo.class, sb.toString(), account);
		}
		@Override
		public void getPage(int status, PageVo<UserInfo> page) {
			SqlQuery sqlQuery = new SqlQuery();
			sqlQuery.setSelect("SELECT id, username, login_account, status, created ");
			sqlQuery.setFrom("FROM user_info ");
			sqlQuery.setWhere("WHERE status=?");
			SqlPageQuery.query(this, UserInfo.class, sqlQuery, page, status);
			
		}
	}
	







