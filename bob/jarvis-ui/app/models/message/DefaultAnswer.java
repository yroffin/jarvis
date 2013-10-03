package models.message;

import java.util.ArrayList;
import java.util.List;

import models.profiling.TransactionProfiler;

import org.codehaus.jackson.annotate.JsonProperty;

import play.data.validation.Constraints.Required;

public class DefaultAnswer extends DefaultBean {
	@Required
	@JsonProperty("answers")
	List<String> answers = new ArrayList<String>();
	@JsonProperty("transactions")
	private List<TransactionProfiler> transactions = new ArrayList<TransactionProfiler>();

	public DefaultAnswer() {
	}

	public void add(String answer) {
		answers.add(answer);
	}

	public TransactionProfiler addTransaction() {
		TransactionProfiler transaction = new TransactionProfiler();
		transactions .add(transaction);
		return transaction;
	}

	@Override
	public String toString() {
		return "DefaultAnswer [answers=" + answers + ", transactions="
				+ transactions + "]";
	}
}
