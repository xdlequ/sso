package com.sso.Dao;

import com.sso.Entity.User;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author ql
 * @date 2019/7/3
 * SpringData Repository接口
 * CrudRepository接口提供了最基本的对实体类的crud操作。
 * - T save(T entity);					//保存单个实体 
 * - T findOne(ID id);					// 根据id查找实体        
 * - void delete(ID/T/Iterable);		// 根据Id删除实体，删除实体，批量删除
 * PagingAndSortingRepository 提供分页与排序功能
 * <T,ID extends Serializable>  // 第一个参数传实体类，第二个参数传注解数据类型
 * Iterable<T> findAll(Sort sort) //排序
 * Page<T> findAll(Pageable pageable); //分页查询（含排序功能）
 * JpaSpecificationExecutor 提供了Specification(封装 JPA Criteria查询条件)的查询功能
 * - List<T> findAll(Specification<T> spec);
 * - Page<T> findAll(Specification<T> spec, Pageable pageable);
 * - List<T> findAll(Specification<T> spec, Sort sort);
 *
 * 开发建议
 * 1. 这里仅列出的是常用方法
 * 2. CrudRepository 中的findAll() 方法要慎用。当数据库中数据量大，多线程脚本调用findAll方法，系统可能会宕机。
 * 3. CrudRepository 中的deletAll()方法要慎用。这是物理删除，现在企业一般采用逻辑删除。
 * 4. PagingAndSortingRepository 和 JpaSpecificationExecutor 能满足大部分业务需求。
 */

public interface UserRepository extends PagingAndSortingRepository<User,Long>,JpaSpecificationExecutor<User> {
    /**
     * SpringData查询方法定义规范
     1. 查询方法名一般以 find | read | get 开头，建议用find
     * 	findByAccount : 通过account查询User
     * 	account是User的属性，拼接时首字母需大写
     2. 支持的关键词有很多比如 Or,Between,isNull,Like,In等
     * 	findByEmailEndingWithAndCreatedDateLessThan : 查询在指定时间前注册，并以xx邮箱结尾的用户
     * 	And : 并且
     * 	EndingWith : 以某某结尾
     * 	LessThan : 小于
     * 注意
     * 若有User(用户表) Platform(用户平台表) 存在一对一的关系，且User表中有platformId字段
     * SpringData 为了区分：
     * findByPlatFormId 	表示通过platformId字段查询
     * findByPlatForm_Id 	表示通过platform实体类中id字段查询
     表的设计，尽量做单表查询，以确保高并发场景减轻数据库的压力。
     */
    /*通过账号查用户信息*/
    User findByAccout(String account);

    //获取指定时间内以xx邮箱结尾的用户信息
    List<User> findByEmailEndingWithAndCreatedDateLessThan(
    String email,String createDate);


    /**
     * 重点知识：使用@Query注解
     * 上面方法虽然简单，（不用写Sql语句），但不支持复杂查询，
     * 其次是命名太长。
     * 1.使用@Query注解实现复杂查询，设置nativeQuery=true，
     * 使查询支持原生sql
     * 2.配合@Modifying 注解实现创建，修改，删除操作
     * 3.SpringData 默认设置事件为只读事务，若要修改数据则需手动
     * 添加事务注解。
     * * 注意
     * 若@Query 中有多个参数，SpringData 提供两种方法：
     * 第一种 ?1 ... ?2 		要求参数顺序一致
     * 第二种 :xxx ... :yyy 	xxx 和 yyy 必须是实体类对应的属性值，不要求参数顺序但参数前要加上@Param("xxx")
     * 模糊查询可使用 %xxx%
     */
    /**获取活跃用户数量*/
    @Query("select count(u.id) from User u where u.platform=:platform and " +
            "u.updatedDate<=:updateDate")
    long getActiveUserCount(@Param("platform")String platform,
                            @Param("updateDate")String updateDate);

    /** 通过邮箱或者手机号模糊查询用户信息*/
    @Query(value="SELECT u FROM User u WHERE u.email LIKE %?1% OR u.iphone LIKE %?2%")
    List<User> findByEmailAndIhpneLike(String email, String iphone);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    void updateUserEmail(@Param("id") Long id, @Param("email") String email);
}



