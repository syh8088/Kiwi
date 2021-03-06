package kiwi.blog.common.config.authentication.metadatasource;

import kiwi.blog.resources.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();
    private ResourceService resourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap, ResourceService resourceService) {
        this.requestMap = resourcesMap;
        this.resourceService = resourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) o).getRequest();

        if (requestMap != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {

                RequestMatcher matcher = entry.getKey();

                if (matcher.matches(request)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();

        for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    // 타입 검사
    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }

    public void reload() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = resourceService.getResourceList();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();

        requestMap.clear();

        while (iterator.hasNext()) {
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
            requestMap.put(entry.getKey(), entry.getValue());
        }
    }
}
