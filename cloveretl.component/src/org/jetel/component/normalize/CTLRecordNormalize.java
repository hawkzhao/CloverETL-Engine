/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.component.normalize;

import java.util.Properties;

import org.jetel.component.TransformUtils;
import org.jetel.ctl.CTLAbstractTransform;
import org.jetel.ctl.CTLEntryPoint;
import org.jetel.ctl.TransformLangExecutorRuntimeException;
import org.jetel.data.DataRecord;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.TransformException;
import org.jetel.metadata.DataRecordMetadata;
import org.jetel.util.ExceptionUtils;

/**
 * Base class of all Java transforms generated by CTL-to-Java compiler from CTL transforms in the Normalizer component.
 *
 * @author Michal Tomcanyi, Javlin a.s. &lt;michal.tomcanyi@javlin.cz&gt;
 * @author Martin Janik, Javlin a.s. &lt;martin.janik@javlin.eu&gt;
 *
 * @version 22nd June 2010
 * @created 2nd April 2009
 *
 * @see RecordNormalize
 */
public abstract class CTLRecordNormalize extends CTLAbstractTransform implements RecordNormalize {

	/** Input data record used for normalization, or <code>null</code> if not accessible. */
	private DataRecord inputRecord = null;
	/** Output data record used for normalization, or <code>null</code> if not accessible. */
	private DataRecord outputRecord = null;

	@Override
	public final boolean init(Properties parameters, DataRecordMetadata sourceMetadata, DataRecordMetadata targetMetadata)
			throws ComponentNotReadyException {
		globalScopeInit();

		return initDelegate();
	}

	/**
	 * Called by {@link #init(Properties, DataRecordMetadata, DataRecordMetadata)} to perform user-specific
	 * initialization defined in the CTL transform. The default implementation does nothing, may be overridden
	 * by the generated transform class.
	 *
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 *
	 * @throws ComponentNotReadyException if the initialization fails
	 */
	@CTLEntryPoint(name = RecordNormalizeTL.INIT_FUNCTION_NAME, required = false)
	protected Boolean initDelegate() throws ComponentNotReadyException {
		// does nothing and succeeds by default, may be overridden by generated transform classes
		return true;
	}

	@Override
	public final int count(DataRecord source) throws TransformException {
		int result = 0;

		// only input record is accessible within the count() function
		inputRecord = source;

		try {
			result = countDelegate();
		} catch (ComponentNotReadyException exception) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #count(DataRecord)} to determine the number of output data records in a user-specific way
	 * defined in the CTL transform. Has to be overridden by the generated transform class.
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = RecordNormalizeTL.COUNT_FUNCTION_NAME, required = true)
	protected abstract Integer countDelegate() throws ComponentNotReadyException, TransformException;

	@Override
	public final int countOnError(Exception exception, DataRecord source) throws TransformException {
		int result = 0;

		// only input record is accessible within the countOnError() function
		inputRecord = source;

		try {
			result = countOnErrorDelegate(TransformUtils.getMessage(exception), ExceptionUtils.stackTraceToString(exception));
		} catch (UnsupportedOperationException ex) {
			// no custom error handling implemented, throw an exception so the transformation fails
			throw new TransformException("Normalization failed!", exception);
		} catch (ComponentNotReadyException ex) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #countOnError(Exception, DataRecord)} to determine the number of output data records in
	 * a user-specific way defined in the CTL transform. May be overridden by the generated transform class.
	 * Throws <code>UnsupportedOperationException</code> by default.
	 *
	 * @param errorMessage an error message of the error message that occurred
	 * @param stackTrace a stack trace of the error message that occurred
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = RecordNormalizeTL.COUNT_ON_ERROR_FUNCTION_NAME, parameterNames = {
			RecordNormalizeTL.ERROR_MESSAGE_PARAM_NAME, RecordNormalizeTL.STACK_TRACE_PARAM_NAME }, required = false)
	protected Integer countOnErrorDelegate(String errorMessage, String stackTrace)
			throws ComponentNotReadyException, TransformException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int transform(DataRecord source, DataRecord target, int idx) throws TransformException {
		int result = 0;

		// both input and output records are accessible within the transform() function
		inputRecord = source;
		outputRecord = target;

		try {
			result = transformDelegate(idx);
		} catch (ComponentNotReadyException exception) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make both input and output records inaccessible again
		inputRecord = null;
		outputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #transform(DataRecord, DataRecord, int)} to transform data records in a user-specific way
	 * defined in the CTL transform. Has to be overridden by the generated transform class.
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = RecordNormalizeTL.TRANSFORM_FUNCTION_NAME, parameterNames = {
			RecordNormalizeTL.IDX_PARAM_NAME }, required = true)
	protected abstract Integer transformDelegate(Integer idx) throws ComponentNotReadyException, TransformException;

	@Override
	public final int transformOnError(Exception exception, DataRecord source, DataRecord target, int idx)
			throws TransformException {
		int result = 0;

		// both input and output records are accessible within the transformOnError() function
		inputRecord = source;
		outputRecord = target;

		try {
			result = transformDelegate(idx);
		} catch (UnsupportedOperationException ex) {
			// no custom error handling implemented, throw an exception so the transformation fails
			throw new TransformException("Normalization failed!", exception);
		} catch (ComponentNotReadyException ex) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make both input and output records inaccessible again
		inputRecord = null;
		outputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #transformOnError(Exception, DataRecord, DataRecord, int)} to transform data records in
	 * a user-specific way defined in the CTL transform. May be overridden by the generated transform class.
	 * Throws <code>UnsupportedOperationException</code> by default.
	 *
	 * @param errorMessage an error message of the error message that occurred
	 * @param stackTrace a stack trace of the error message that occurred
	 * @param idx sequential number of output record (starting from 0)
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = RecordNormalizeTL.TRANSFORM_ON_ERROR_FUNCTION_NAME, parameterNames = {
			RecordNormalizeTL.ERROR_MESSAGE_PARAM_NAME, RecordNormalizeTL.STACK_TRACE_PARAM_NAME,
			RecordNormalizeTL.IDX_PARAM_NAME }, required = false)
	protected Integer transformOnErrorDelegate(String errorMessage, String stackTrace, Integer idx)
			throws ComponentNotReadyException, TransformException {
		throw new UnsupportedOperationException();
	}

	@Override
	@CTLEntryPoint(name = RecordNormalizeTL.CLEAN_FUNCTION_NAME, required = false)
	public void clean() {
		// does nothing by default, may be overridden by generated transform classes
	}

	@Override
	protected final DataRecord getInputRecord(int index) {
		if (inputRecord == null) {
			throw new TransformLangExecutorRuntimeException(INPUT_RECORDS_NOT_ACCESSIBLE);
		}

		if (index != 0) {
			throw new TransformLangExecutorRuntimeException(new Object[] { index }, INPUT_RECORD_NOT_DEFINED);
		}

		return inputRecord;
	}

	@Override
	protected final DataRecord getOutputRecord(int index) {
		if (outputRecord == null) {
			throw new TransformLangExecutorRuntimeException(OUTPUT_RECORDS_NOT_ACCESSIBLE);
		}

		if (index != 0) {
			throw new TransformLangExecutorRuntimeException(new Object[] { index }, OUTPUT_RECORD_NOT_DEFINED);
		}

		return outputRecord;
	}

}
