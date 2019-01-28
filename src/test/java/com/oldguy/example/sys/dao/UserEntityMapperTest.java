package com.oldguy.example.sys.dao;

import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import com.oldguy.example.modules.sys.dao.jpas.UserEntityMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserEntityMapperTest {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Test
    public void testSave() {

        UserEntity entity = new UserEntity();
        entity.setUserId("测试2");
        entity.setUsername("测试2");
        entity.setCreateTime(new Date());
        entity.setStatus(1);

        userEntityMapper.save(entity);
    }
}
