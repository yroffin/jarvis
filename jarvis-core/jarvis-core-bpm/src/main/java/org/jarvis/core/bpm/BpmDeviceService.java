package org.jarvis.core.bpm;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.resources.api.device.ApiDeviceResources;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * bpm service delegate
 * this class could be serialized by camunda
 * so all must be transient (and we have to call spring by context all the time)
 */
@SuppressWarnings("serial")
public class BpmDeviceService implements Serializable {

	/**
	 * @param value 
	 * @return List<DeviceBean>
	 * @throws TechnicalNotFoundException 
	 */
	public DeviceBean get(String value) throws TechnicalNotFoundException {
		ApiDeviceResources bean = BpmRunListener.getContext().getBean(ApiDeviceResources.class);
		return bean.doGetByIdBean(value);
	}

	/**
	 * @param device 
	 * @return List<DeviceBean>
	 * @throws TechnicalException 
	 */
	public GenericMap execute(DeviceBean device) throws TechnicalException {
		ApiDeviceResources bean = BpmRunListener.getContext().getBean(ApiDeviceResources.class);
		try {
			return bean.execute(device, new GenericMap("{}"));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		} catch (IOException e) {
			throw new TechnicalException(e);
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @param value 
	 * @return List<DeviceBean>
	 * @throws TechnicalNotFoundException 
	 */
	public DeviceBean getByName(String value) throws TechnicalNotFoundException {
		ApiDeviceResources bean = BpmRunListener.getContext().getBean(ApiDeviceResources.class);
		List<DeviceBean> list = bean.doFindByAttributeBean("name", value);
		if(list.size() == 0) throw new TechnicalNotFoundException(value);
		return list.get(0);
	}

	/**
	 * @return List<DeviceBean>
	 */
	public List<DeviceBean> find() {
		ApiDeviceResources bean = BpmRunListener.getContext().getBean(ApiDeviceResources.class);
		return bean.doFindAllBean();
	}

	@Override
	public String toString() {
		return "BpmDeviceService []";
	}
}
