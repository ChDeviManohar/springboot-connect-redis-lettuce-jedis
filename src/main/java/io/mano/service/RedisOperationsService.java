package io.mano.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisOperationsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisOperationsService.class);
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	
	public void performRedisOperations() {
		redisStringOps();
		redisSetOps();
		redisListOps();
		redisHashOps();
		redisMultiOps();
		redisSetGetMultiOps();
	}
	
	private void redisStringOps() {
		LOGGER.info("-----String Operations::Start");
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		LOGGER.info("---Storing only one string at a time--");
		valueOps.set("keyOne", "valueOne");
		valueOps.set("keyTwo", "valueTwo");
		LOGGER.info("stored value for keyOne:{}", valueOps.get("keyOne"));
		LOGGER.info("stored value for keyTwo:{}", valueOps.get("keyTwo"));
		
		LOGGER.info("---Storing multiple strings at a time--");
		Map<String, String> multiValsMap = new HashMap<>();
		multiValsMap.put("keyThree", "valueThree");
		multiValsMap.put("keyFour", "valueFour");
		valueOps.multiSet(multiValsMap);
		
		LOGGER.info("---Display all keys available in redis--");
		Set<String> keys = redisTemplate.keys("*");
		LOGGER.info("keys: {}", keys);
		
		LOGGER.info("---Deleting from redis--");
		LOGGER.info("No.of keys data deleted: {}", redisTemplate.delete(keys));
		LOGGER.info("-----String Operations::End");
	}

	private void redisSetOps() {
		LOGGER.info("-----Set Operations::Start");
		SetOperations<String, String> setOps = redisTemplate.opsForSet();
		LOGGER.info("Size of setOne :{}", setOps.members("setOne").size());
		setOps.add("setOne", "VAL-1", "VAL-2", "SO-3", "SO-4");
		LOGGER.info("after setting values Size of setOne :{}", setOps.members("setOne").size());
		LOGGER.info("setOne values:{}", setOps.members("setOne"));
		
		LOGGER.info("Size of setTwo :{}", setOps.members("setTwo").size());
		setOps.add("setTwo", "VAL-1", "VAL-2", "ST-3", "ST-4");
		LOGGER.info("after setting values Size of setTwo :{}", setOps.members("setTwo").size());
		LOGGER.info("setOne values:{}", setOps.members("setTwo"));
		
		LOGGER.info("Intersection of setOne and setTwo values: {}", setOps.intersect("setOne", "setTwo"));
		
		LOGGER.info("Remove all redis keys data, no.of keys data removed: {}", redisTemplate.delete(redisTemplate.keys("*")));
		
		LOGGER.info("-----Set Operations::End");
	}
	
	private void redisListOps() {
		LOGGER.info("-----List Operations::End");
		ListOperations<String, String> listOps = redisTemplate.opsForList();
		LOGGER.info("List size initially:{}", listOps.range("listOne", 0, -1).size());
		listOps.leftPush("listOne", "one");
		listOps.leftPush("listOne", "two");
		listOps.rightPush("listOne", "three");
		listOps.rightPush("listOne", "four");
		LOGGER.info(" listOne values:{}", listOps.range("listOne", 0, -1));
		LOGGER.info("keys deleted for listOne : {}", redisTemplate.delete("listOne"));
		LOGGER.info("-----List Operations::End");
	}
	
	private void redisHashOps() {
		LOGGER.info("-----Hash Operations::Start");
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
		String empJoeKey = "emp:joe";
		Map<String, String> empJoeMap = new HashMap<>();
		empJoeMap.put("name", "Joe");
		empJoeMap.put("age", "30");
		empJoeMap.put("id", "12345");
		
		hashOps.putAll(empJoeKey, empJoeMap);
		
		LOGGER.info("Get emp Joe details: {}", hashOps.entries(empJoeKey));
		LOGGER.info("Get emp Joe age: {}", hashOps.get(empJoeKey, "age"));
		LOGGER.info("keys deleted for {} : {}", empJoeKey,  redisTemplate.delete(empJoeKey));
		LOGGER.info("-----Hash Operations::End");
	}
	
	private void redisMultiOps() {
		LOGGER.info("-----Multi Operations::Start");
		List<Object> txResults = new ArrayList<>();
		
		txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public  List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi();
				
				operations.opsForValue().set("strOne", "strOneValue");
				operations.opsForValue().set("strTwo", "strTwoValue");
				
				operations.opsForSet().add("setOne", "SetVal-1", "SetVal-2", "SetVal-3");
				operations.opsForSet().members("setOne");
				
				operations.opsForList().leftPush("listKey", "listVal-1");
				operations.opsForList().rightPush("listKey", "listVal-2");
				operations.opsForList().range("listKey", 0, -1);
				operations.delete("strOne");
				operations.delete("strTwo");
				operations.delete("setOne");
				operations.delete("listKey");
				return operations.exec();
			}
		});
		
		LOGGER.info("No.of items added to set: {}", txResults);
		if(!txResults.isEmpty()) {
			LOGGER.info("Set values: {}",  txResults.get(3));
			LOGGER.info("List values: {}",  txResults.get(6));
		}
		
		LOGGER.info("-----Multi Operations::End");
	}
	
	private void redisSetGetMultiOps() {
		// TODO Auto-generated method stub
		
	}

	

	


}
