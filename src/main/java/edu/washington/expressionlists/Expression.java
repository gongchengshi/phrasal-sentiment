package edu.washington.expressionlists;

public class Expression {

	private String text;
	private String category;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Expression(String text) {
		this.text = text;
	}
	public Expression(String text, String category) {
		super();
		this.text = text;
		this.category = category;
	}
	
}
