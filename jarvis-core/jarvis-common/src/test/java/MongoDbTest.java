import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDbTest {
	protected Logger logger = LoggerFactory.getLogger(MongoDbTest.class);

	@Test
	public void test() {
		logger.warn("Message de test");
	}

}
