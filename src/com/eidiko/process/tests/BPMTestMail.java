package com.eidiko.process.tests;

public class BPMTestMail {
	
	public static void main(String...s)throws Exception{
		
		BPDProcessTests bpdProcessTests = new BPDProcessTests();
		
		BPDProcessTests.setUpClass();
		bpdProcessTests.runningVeryImportantClientRequest();
		bpdProcessTests.runningImportantClientRequest();
		bpdProcessTests.runningNotSoImportantClientRequest();
		
		
		
		
		
	}

}
