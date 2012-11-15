package org.molgenis.mutation.web;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;

public class MutationFrontController extends app.servlet.FrontController {
	private static final long serialVersionUID = 3141439968743510237L;

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response)
			throws ParseException, DatabaseException, IOException {
		HttpServletRequest req = request.getRequest();
		String contextPath = req.getContextPath();
		String path = req.getRequestURI().substring(contextPath.length() + 1);

		if (path.equals("")) {
			path = "/";
		}

		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		for (String p : services.keySet()) {
			if (path.startsWith(p)) {
				long startTime = System.currentTimeMillis();

				System.out.println("> new request to '" + path + "' from "
						+ request.getRequest().getRemoteHost() + " handled by "
						+ services.get(p).getClass().getSimpleName()
						+ " mapped on path " + p);
				System.out.println("request content: " + request.toString());

				try {
					this.createDatabase(request);
					this.createLogin(request);

					System.out.println("database status: "
							+ (request.getDatabase().getLogin()
									.isAuthenticated() ? "authenticated as "
									+ request.getDatabase().getLogin()
											.getUserName()
									: "not authenticated"));

					request.setRequestPath(path); // the path that was requested
					request.setServicePath(p); // the path mapping used to
												// handle the request
					services.get(p).handleRequest(request, response);
				} catch (Exception e) {
					System.out.println(">>> Ooops... Something went wrong.");
					e.printStackTrace();
				} finally {
					if (request.getDatabase() != null && request.getDatabase().getEntityManager().isOpen()) {
						request.getDatabase().close();
					}
				}

				System.out.println("< request was handled in "
						+ (System.currentTimeMillis() - startTime) + "ms.");

				return;
			}
		}
	}
}