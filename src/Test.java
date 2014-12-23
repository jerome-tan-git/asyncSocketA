import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;

public class Test {
	public static void main(String[] args) throws Exception {
		// Use pool of two threads
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
		Async async = Async.newInstance().use(threadpool);

		Request[] requests = new Request[] {
				Request.Get("http://www.google.com/"),
				Request.Get("http://www.yahoo.com/"),
				Request.Get("http://www.apache.com/"),
				Request.Get("http://www.apple.com/") };

		Queue<Future<Content>> queue = new LinkedList<Future<Content>>();
		// Execute requests asynchronously
		for (final Request request : requests) {
			Future<Content> future = async.execute(request,
					new FutureCallback<Content>() {

						public void failed(final Exception ex) {
							System.out.println(ex.getMessage() + ": " + request);
						}

						public void completed(final Content content) {
							System.out.println("Request completed: " + request);
						}

						public void cancelled() {
						}

					});
			queue.add(future);
		}

		while (!queue.isEmpty()) {
			Future<Content> future = queue.remove();
			try {
				future.get();
			} catch (ExecutionException ex) {
			}
		}
		System.out.println("Done");
		threadpool.shutdown();
	}
}
