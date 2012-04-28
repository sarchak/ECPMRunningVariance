package com.shrikar.library;


public class ECPMStats {
	private int events;
	double old_mean, new_mean, old_variance, new_variance, sum;
	
	public ECPMStats(){
		events=0;
		sum = old_mean = new_mean=old_variance=new_variance = 0;
	}
	
	void add(double ecpm){
		events++;
		sum += ecpm;
		if(events == 1){
			old_mean = new_mean = ecpm;
			old_variance = 0;
		} else {
			new_mean = old_mean + (ecpm - old_mean)/events;
			new_variance = old_variance + (ecpm - old_mean) * (ecpm - new_mean);
			old_mean = new_mean;
			old_variance = new_variance;
		}
	}
	
	int numEvents(){
		return events;
	}
	
	double ecpmMean(){
		if(events > 0)
			return new_mean;
		else
			return 0;
	}
	
	double ecpmVariance(){
		if(events>1){
			return new_variance/(events-1);
		} else {
			return 0;
		}
	}
	
	double sum(){
		return sum;
	}
	
}
