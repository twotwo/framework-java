/**
 * Create at Jul 19, 2013
 */
package com.li3huo.jmeter;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.li3huo.util.HttpTool;


/**
 * @author liyan
 * 
 */
public class CustomizeJavaClient extends AbstractJavaSamplerClient implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6384765655590726062L;

	private static final Logger LOG = LoggingManager.getLoggerForClass();

	private String url;
	private static final String URL_NAME = "url";
//	private static final String DEFAULT_URL_NAME = "http://localhost:8005/console";
	private static final String DEFAULT_URL_NAME = "http://baidu.com";
	/** The base number of milliseconds to sleep during each sample. */
	private long sleepTime;

	/** The default value of the SleepTime parameter, in milliseconds. */
	public static final long DEFAULT_SLEEP_TIME = 100;

	/** The name used to store the SleepTime parameter. */
	private static final String SLEEP_NAME = "Sleep_Time";

	/**
	 * A mask to be applied to the current time in order to add a semi-random
	 * component to the sleep time.
	 */
	private long sleepMask;

	/** The default value of the SleepMask parameter. */
	public static final long DEFAULT_SLEEP_MASK = 0xff;

	/** The name used to store the SleepMask parameter. */
	private static final String MASK_NAME = "Sleep_Mask";

	/** Formatted string representation of the default SleepMask. */
	private static final String DEFAULT_MASK_STRING = "0x"
			+ (Long.toHexString(DEFAULT_SLEEP_MASK))
					.toUpperCase(java.util.Locale.ENGLISH);

	/** The label to store in the sample result. */
	private String label;

	/** The default value of the Label parameter. */
	// private static final String LABEL_DEFAULT = "JavaTest";
	/** The name used to store the Label parameter. */
	private static final String LABEL_NAME = "Label";

	/** The response message to store in the sample result. */
	private String responseMessage;

	/** The default value of the ResponseMessage parameter. */
	private static final String RESPONSE_MESSAGE_DEFAULT = "";

	/** The name used to store the ResponseMessage parameter. */
	private static final String RESPONSE_MESSAGE_NAME = "ResponseMessage";

	/** The response code to be stored in the sample result. */
	private String responseCode;

	/** The default value of the ResponseCode parameter. */
	private static final String RESPONSE_CODE_DEFAULT = "";

	/** The name used to store the ResponseCode parameter. */
	private static final String RESPONSE_CODE_NAME = "ResponseCode";

	/** The sampler data (shown as Request Data in the Tree display). */
	private String samplerData;

	/** The default value of the SamplerData parameter. */
	private static final String SAMPLER_DATA_DEFAULT = "";

	/** The name used to store the SamplerData parameter. */
	private static final String SAMPLER_DATA_NAME = "SamplerData";

	/** Holds the result data (shown as Response Data in the Tree display). */
	private String resultData;

	/** The default value of the ResultData parameter. */
	private static final String RESULT_DATA_DEFAULT = "";

	/** The name used to store the ResultData parameter. */
	private static final String RESULT_DATA_NAME = "ResultData";

	/** The success status to be stored in the sample result. */
	private boolean success;

	/** The default value of the Success Status parameter. */
	private static final String SUCCESS_DEFAULT = "OK";

	/** The name used to store the Success Status parameter. */
	private static final String SUCCESS_NAME = "Status";

	/*
	 * Utility method to set up all the values
	 */
	private void setupValues(JavaSamplerContext context) {

		url = context.getParameter(URL_NAME, DEFAULT_URL_NAME);

		sleepTime = context.getLongParameter(SLEEP_NAME, DEFAULT_SLEEP_TIME);
		sleepMask = context.getLongParameter(MASK_NAME, DEFAULT_SLEEP_MASK);

		responseMessage = context.getParameter(RESPONSE_MESSAGE_NAME,
				RESPONSE_MESSAGE_DEFAULT);

		responseCode = context.getParameter(RESPONSE_CODE_NAME,
				RESPONSE_CODE_DEFAULT);

		success = context.getParameter(SUCCESS_NAME, SUCCESS_DEFAULT)
				.equalsIgnoreCase("OK");

		label = context.getParameter(LABEL_NAME, "");
		if (label.length() == 0) {
			label = context.getParameter(TestElement.NAME); // default to name
															// of element
		}

		samplerData = context.getParameter(SAMPLER_DATA_NAME,
				SAMPLER_DATA_DEFAULT);

		resultData = context
				.getParameter(RESULT_DATA_NAME, RESULT_DATA_DEFAULT);
	}

	/**
	 * Provide a list of parameters which this test supports. Any parameter
	 * names and associated values returned by this method will appear in the
	 * GUI by default so the user doesn't have to remember the exact names. The
	 * user can add other parameters which are not listed here. If this method
	 * returns null then no parameters will be listed. If the value for some
	 * parameter is null then that parameter will be listed in the GUI with an
	 * empty value.
	 * 
	 * @return a specification of the parameters used by this test which should
	 *         be listed in the GUI, or null if no parameters should be listed.
	 */
	@Override
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument(URL_NAME, DEFAULT_URL_NAME);
		params.addArgument(SLEEP_NAME, String.valueOf(DEFAULT_SLEEP_TIME));
		params.addArgument(MASK_NAME, DEFAULT_MASK_STRING);
		params.addArgument(LABEL_NAME, "");
		params.addArgument(RESPONSE_CODE_NAME, RESPONSE_CODE_DEFAULT);
		params.addArgument(RESPONSE_MESSAGE_NAME, RESPONSE_MESSAGE_DEFAULT);
		params.addArgument(SUCCESS_NAME, SUCCESS_DEFAULT);
		params.addArgument(SAMPLER_DATA_NAME, SAMPLER_DATA_DEFAULT);
		params.addArgument(RESULT_DATA_NAME, SAMPLER_DATA_DEFAULT);
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#setupTest
	 * (org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
	 */
	@Override
	public void setupTest(JavaSamplerContext context) {

		if (LOG.isDebugEnabled()) {
			LOG.debug((new StringBuilder()).append(whoAmI())
					.append("\tsetupTest()").toString());
			listParameters(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#
	 * teardownTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
	 */
	@Override
	public void teardownTest(JavaSamplerContext context) {
		// TODO Auto-generated method stub
		super.teardownTest(context);
	}

	/**
	 * Perform a single sample.<br>
	 * In this case, this method will simply sleep for some amount of time.
	 * 
	 * This method returns a <code>SampleResult</code> object.
	 * 
	 * <pre>
	 * 
	 *  The following fields are always set:
	 *  - responseCode (default &quot;&quot;)
	 *  - responseMessage (default &quot;&quot;)
	 *  - label (set from LABEL_NAME parameter if it exists, else element name)
	 *  - success (default true)
	 * 
	 * </pre>
	 * 
	 * The following fields are set from the user-defined parameters, if
	 * supplied:
	 * 
	 * <pre>
	 * -samplerData - responseData
	 * </pre>
	 * 
	 * @see org.apache.jmeter.samplers.SampleResult#sampleStart()
	 * @see org.apache.jmeter.samplers.SampleResult#sampleEnd()
	 * @see org.apache.jmeter.samplers.SampleResult#setSuccessful(boolean)
	 * @see org.apache.jmeter.samplers.SampleResult#setSampleLabel(String)
	 * @see org.apache.jmeter.samplers.SampleResult#setResponseCode(String)
	 * @see org.apache.jmeter.samplers.SampleResult#setResponseMessage(String)
	 * @see org.apache.jmeter.samplers.SampleResult#setResponseData(byte [])
	 * @see org.apache.jmeter.samplers.SampleResult#setDataType(String)
	 * 
	 * @param context
	 *            the context to run with. This provides access to
	 *            initialization parameters.
	 * 
	 * @return a SampleResult giving the results of this sample.
	 */
	public SampleResult runTest(JavaSamplerContext context) {
		setupValues(context);
		SampleResult results = new SampleResult();
		
		results.setResponseCode(responseCode);
		results.setResponseMessage(responseMessage);
		results.setSampleLabel(label);

		try {
			resultData = HttpTool.doGetRequest(url, null);
		} catch (Exception ex) {
		}
		
		System.out.println("response size="+resultData.length());

		if (samplerData != null && samplerData.length() > 0) {
			results.setSamplerData(samplerData);
		}

		if (resultData != null && resultData.length() > 0) {
			results.setResponseData(resultData, null);
			results.setDataType(SampleResult.TEXT);
		}

		// Record sample start time.
		results.sampleStart();

		long sleep = sleepTime;
		if (sleepTime > 0 && sleepMask > 0) { // / Only do the calculation if
												// it is needed
			long start = System.currentTimeMillis();
			// Generate a random-ish offset value using the current time.
			sleep = sleepTime + (start % sleepMask);
		}

		try {
			// Execute the sample. In this case sleep for the
			// specified time, if any
			if (sleep > 0) {
				Thread.sleep(sleep);
			}
			results.setSuccessful(success);
		} catch (InterruptedException e) {
			LOG.warn("JavaTest: interrupted.");
			results.setSuccessful(true);
		} catch (Exception e) {
			LOG.error("JavaTest: error during sample", e);
			results.setSuccessful(false);
		} finally {
			// Record end time and populate the results.
			results.sampleEnd();
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(whoAmI() + "\trunTest()" + "\tTime:\t"
					+ results.getTime());
			listParameters(context);
		}

		return results;
	}

	private void listParameters(JavaSamplerContext context) {
		String name;
		for (Iterator<String> argsIt = context.getParameterNamesIterator(); argsIt
				.hasNext(); LOG.debug((new StringBuilder()).append(name)
				.append("=").append(context.getParameter(name)).toString()))
			name = (String) argsIt.next();

	}

	private String whoAmI() {
		StringBuilder sb = new StringBuilder();
		sb.append(Thread.currentThread().toString());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		return sb.toString();
	}

}
