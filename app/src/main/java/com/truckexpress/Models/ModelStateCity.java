package com.truckexpress.Models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ModelStateCity{

	@SerializedName("array")
	private List<ArrayItem> array;

	public void setArray(List<ArrayItem> array){
		this.array = array;
	}

	public List<ArrayItem> getArray(){
		return array;
	}
}