package edu.washington.expressionlists.types;

public abstract class Expression {

	protected String text;
	protected String category;
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
		this.text = text;
		this.category = category;
	}
	}
