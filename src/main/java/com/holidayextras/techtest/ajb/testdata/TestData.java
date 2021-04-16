package com.holidayextras.techtest.ajb.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.holidayextras.techtest.ajb.domain.ProtoUser;

/**
 * In-memory store for test data, which may or may not have been activated (converted into actual database records).
 */
@Component
public class TestData {
	private List<TestDataset> datasets = new ArrayList<>();
	
	@PostConstruct
	public void populateStaticTestData() {
		var user = new ProtoUser("a@a.com", "Adam", "Burley");
		List<ProtoUser> userList = Arrays.asList(user);
		datasets.add(new TestDataset(false, false, userList.size(), userList));
		datasets.add(new TestDataset(false, true, 50, null));
	}
	
	public synchronized int addTestData(TestDataset dataset) {
		datasets.add(dataset);
		return datasets.size()-1;
	}
	
	public TestDataset getTestDataset(int id) {
		if ((id >= 0) && (id < datasets.size())) {
			return datasets.get(id);
		} else {
			return null;
		}
	}
	
	public void markAsActivated(int id) {
		datasets.get(id).setActivated(true);
	}
	
	public void deactivateAll() {
		for (TestDataset dataset : datasets) {
			dataset.setActivated(false);
		}
	}
}
