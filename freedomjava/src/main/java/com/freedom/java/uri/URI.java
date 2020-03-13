/**
 * Copyright 2014 Freedom-Loader Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freedom.java.uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import com.freedom.java.string.StringWriter;
import android.net.*;

import com.freedom.java.*;

public class URI {
	private final static String RAND_NAME = "&";
	private final static String SEMI_NAME = ";";
	private final static String MAKE_NAME = "?";
	public final static String DoubleQuote = "\"";
	public final static String Separator = System.getProperty("line.separator");
	public final static String Uri_Word = "uri_word";
	public final static String ADD_LINES_NUMBER = "addLinesNumber";
	private boolean cUriOnWork = false;
	private long startDate;

	private long endDate;
	Map<String, String> removeCount = new LinkedHashMap<String, String>();

	public URI() {

	}

	public URI(String uri) {
		parseWord(uri);
	}

	public URI setQueryMap(Map<String, List<String>> uriQueryMap) {
		URI queryString = new URI();
		for (Map.Entry<String, List<String>> entry : uriQueryMap.entrySet()) {
			queryString.uriQueryMap.put(entry.getKey(),new ArrayList<String>(entry.getValue()));
		}
		return queryString;
	}

	/**
	 * Set charsetName.
	 * <p>
	 * @param charset charsetName
	 * @return self
	 */
	public URI setCharName(String charset) {
		CHARSET_NAME = charset;
		return this;
	}

	/**
	 * create URI and add the query of the URI.
	 * <p>
	 * @param uri URI string to add to map
	 * @return self
	 */
	public static URI parse(final java.net.URI uri) {
		return parse(uri.toString());
	}

	/**
	 * create URI and add the query of the URI.
	 * <p>
	 * @param uri URI string to add to map
	 * @return self
	 */
	public static URI parse(final Uri uri) {
		return parse(uri.toString());
	}

	/**
	 * create URI and add the query of the URI.
	 * <p>
	 * @param uri giving URI
	 * query query string to add
	 * @return self
	 */
	public static URI parse(final CharSequence uri) {
		URI encodeUri = new URI();
		//remove query if any
		encodeUri.uri = encodeUri.removeQuery(uri.toString());

		try {
			//Retrieve query if any
			encodeUri.doQueryWork(retrieveQuery(uri.toString()), UseType.APPEND);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodeUri;
	}

	public URI parseWord(String uri) {
		this.uri = uri;
		clearRemoveCount();

		this.startDate = 0;
		this.endDate = 0;
		return this;
	}

	private boolean onStartWork(boolean isStart) {
		long current = System.currentTimeMillis();
		if (isStart) {
			this.startDate = current;
			this.endDate = current;
		} else {
			this.endDate = current;
		}
		return true;
	}

	public URI changeUriOnWork() {
		changeUriOnWork(true);
		return this;
	}

	public URI changeUriOnWork(boolean changeUriOnWork) {
		this.cUriOnWork = changeUriOnWork;
		return this;
	}

	/**
	 * Remove query scheme from URI and return URI domain
	 *
	 * @param uri given URI
	 * @return new URI
	 */
	public static String stripQueryStringAndHashFromPath(String uri) {
		return uri.replaceAll(("(\\?.*|\\#.*)"), "");
	}

	/**
	 * Encode URI
	 *
	 * @param uri given URI to encoded
	 * @return encodedUri
	 */
	public static String encodeUri(String uri) {
		String encodedUri = Uri.encode(uri, "@#&=*+-_.,:!?()/~'%");
		return encodedUri;
	}

	/**
	 * Check if URI has Query
	 *
	 * @see #isEmpty()
	 * @return
	 */
	public boolean hasQuery() {
		return !isEmptyQuery() ? true : false;
	}

	/**
	 * Find the giving key inside query parameter if exits Returns {@code value}
	 * else if it does not exist, Returns {@code false}
	 * <p>
	 *
	 * @see #get(String)
	 * @param key key of the parameter to be find through query
	 * @return query in ("Boolean",true,false)
	 */
	public boolean getBoolean(final String key) {
		String queryBoolean = get(key);
		return queryBoolean.equals("true");
	}

	/**
	 * Find the giving key inside query parameter if exits Returns {@code value}
	 * else if it does not exist, Returns {@code 0}
	 * <p>
	 *
	 * @see #get(String)
	 * @param key key of the parameter to be find through query
	 * @return query in ("Integer",123456789)
	 */
	public int getInt(final String key) {
		String queryBoolean = get(key);
		return Integer.valueOf(queryBoolean);
	}

	/**
	 * Find the giving key inside query parameter if exits Returns {@code value}
	 * else if it does not exist, Returns {@code null}
	 * <p>
	 * if only single value of the parameter with giving key find then return value of the key
	 *
	 * You can also validate if key is find using {contains(key)}
	 * <p>
	 *
	 * @param key key of the parameter to be find through query
	 * @return <tt>true</tt> - return the value in key; <tt>false</tt> - return null
	 *        if key is not find in query
	 */
	public String get(final String key) {
		List<String> value = getQueryParameters(key);
		if (value == null || value.isEmpty()) {
			return null;
		}
		return value.get(0);
	}

	/**
	 * Sets a new query parameter {#add(key, value,type)}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value;  - else if not exits then create new one.
	 *
	 * @param key key of the query parameter
	 * @param value value of the query parameter.
	 * @return self
	 */
	public URI add(final String key, final String value) {
		doValues(key,value,UseType.ADD);
		return this;
	}

	/**
	 * Sets a new query parameter {#add(key, value,type)}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value; - else if not exits then create new one.
	 *
	 * <p>This can only be use when using {@code integer}, {#add("key",323323223)}:
	 * </p> 
	 *
	 * @param key key of the query parameter
	 * @param value value of the query parameter.
	 * @return self
	 */
	public URI add(final String key, final int value) {
		doValues(key,String.valueOf(value),UseType.ADD);
		return this;
	}

	/**
	 * Sets a new query parameter {#add(query)}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value; 
	 *
	 * @param query query to add to map.
	 * @return self
	 */
	public URI add(final String query) {
		doQueryWork(query, UseType.ADD);
		return this;
	}

	/**
	 * Sets a new query parameter {#add(key, value,type)}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value;  - else if not exits then create new one.
	 *
	 * @param key key of the query parameter
	 * @param value value of the query parameter.
	 * @param type if request should append or add or reset or remove
	 * @return self
	 */
	public URI add(String key,String value,UseType type) {
		doValues(key,value,type);
		return this;
	}

	/**
	 * Sets fast method to append to query.
	 * <p>
	 * @param uri given URI
	 * @param key key of the query parameter
	 * @param value value of the query parameter.
	 * @return query parameters
	 */
	public String appendToExitingQuery(String uri, String key,String value) {
		return  uri.indexOf('=') > 0 ? uri+"&" : uri+"?" + key + "=" + value;
	}

	/**
	 * append a new query with string value example{@code key=value} integer.
	 *  <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value;  - else if not exits then create new one.
	 *
	 * <p>This can only be use when using {@code integer}, {#add("key",323323223)}:
	 * </p> 
	 *
	 * @param key {@code key} of the query parameter
	 * @param value {@code value} of the query parameter in number or integer
	 * @return self
	 */
	public URI append(final String key, final int value) {
		return append(key,String.valueOf(value));
	}

	/**
	 * append a new query with string value example{@code key=value}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value;  - else if not exits then create new one.
	 *
	 * @param key {@code key} of the query parameter
	 * @param value {@code value} of the query parameter
	 * @return self
	 */
	public URI append(final String key, final String value ) {
		doValues(key,value,UseType.APPEND);
		return this;
	}

	/**
	 * append query with string value {@code key=value}.
	 * <p>
	 * if such parameters with new key already exist in map query, then remove it and then
	 * add new to the query parameters with new value;  - else if not exits then create new one.
	 *
	 * @param query query parameter
	 * @return self
	 */
	public URI append(final String query) {
		doQueryWork(query, UseType.APPEND);
		return this;
	}

	/**
	 * Check if query is empty.
	 *
	 * if true mean query has parameters with value and key
	 * <p>This method can only be call only when request URI with {parse}.</p>
	 *
	 * @return <tt>true</tt> - if the query has no parameters; <tt>false</tt> - query has no parameters
	 */
	public boolean isEmptyQuery() {
		return uriQueryMap.isEmpty();
	}

	public boolean isEmpty() {
		return isEmpty(uri);
	}

	/**
	 * Removes the key from query map.
	 * <p>
	 * check if have multiple key if true remove it all.
	 *
	 * @param key key to remove form query map
	 * @return self
	 */
	public URI remove(final String key) {
		doValues(key,null,UseType.REMOVE);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof URI)) {
			return false;
		}

		String urlQuery = toString();
		String encodeUri = ((URI) obj).toString();

		return urlQuery.equals(encodeUri);
	}

	/**
	 * Get string query and return in hash for query map
	 * @return hash code of value for this query map.
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Returns query parameters.
	 *
	 * @return URI with query parameters
	 *        in query map use method (url.indexOf('=') > 0 ? "&" : "?";)
	 *        to get the correct URI with query
	 */
	public String getQuery() {
		StringBuilder builder = getQuery(QuerySeparators.RAND,"&");
		return builder.toString();
	}

	/**
	 * Returns URI.
	 *
	 * @param value value use to get URI
	 * @return decoded URI
	 */
	public String getUri(URIValue value) {
		return doRealQueryWork(uri,value);
	}

	/**
	 * Returns URL.
	 *
	 * @return URL
	 */
	public String getUri() {
		StringBuilder builder = new StringBuilder();

		if(uri != null) {
			builder.append(uri);
		}
		return builder.toString();
	}

	/**
	 * print out URL.
	 *
	 * @return {com.android.freedom.StringWriter.StringWriter StringWriter}
	 */
	public String printOut() {
		StringWriter writer = null;
		try {
			writer = new StringWriter()
					.append("===============START OR URL ===============")
					.append(" path: ", getPath())
					.append(" scheme: ",getScheme())
					.append(" no scheme: ",removeScheme())
					.append(" authority: ", getAuthority())
					.append("===============END OF URL ===============")
					.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	/**
	 * Returns the keys with value.
	 *
	 * @return String
	 */
	public String printOutAll() {
		StringWriter writer = new StringWriter();
		Map<String, List<String>> mapNames = getUrlQueryMap();

		for (String key : mapNames.keySet()) {
			List<String> valueNames = mapNames.get(key);
			if (valueNames != null && !valueNames.isEmpty()) {
				writer.append(key+": ",valueNames.get(0));
			}
		}
		return writer.toString();
	}

	/**
	 * Returns the URI string of the query parameters.
	 *
	 * @return UriQuery
	 */
	@Override
	public String toString() {
		return toString(QuerySeparators.RAND,null);
	}

	/**
	 * Returns the URI string of the query parameters.
	 *
	 * @param other other string to be use for separator
	 * @return UriQuery
	 */
	public String toString(String other) {
		return toString(QuerySeparators.OTHER,other);
	}

	/**
	 * Returns the URI string of the query parameters.
	 *
	 * @param type separator to be use between uri parameters
	 * @return UriQuery
	 */
	public String toString(QuerySeparators type) {
		return toString(type,MAKE_NAME);
	}

	/**
	 * Returns the URI string of the query parameters.
	 *
	 * @param type separator to be use between URI parameters
	 * @param other other string to be use for separator
	 *
	 * @return UriQuery
	 */
	public String toString(QuerySeparators type,String other) {
		StringBuilder builder = getQuery(type,other);
		String query = builder.toString();
		String xx = query.equals("") ? "" : !query.startsWith("?") ? "?" : "";
		return getUri()+xx+query;
	}

	/**
	 * Returns the query parameters.
	 *
	 * @param type separator to be use between URI parameters
	 * @param other other string to be use for separator
	 *
	 * @return builder
	 */
	private StringBuilder getQuery(QuerySeparators type,String other) {
		StringBuilder builder = new StringBuilder();
		for(String keys : getQueryParameterKeys()) {
			for (String values : getQueryParameters(keys)) {
				if (builder.length() != 0) {
					builder.append(type == QuerySeparators.QUERY ? MAKE_NAME : type == QuerySeparators.SEMI ? SEMI_NAME :
							type == QuerySeparators.RAND ? RAND_NAME : type == QuerySeparators.OTHER ? other : RAND_NAME);
				}
				try {
					builder.append(encode(keys));

					if (values != null) {
						builder.append('=');
						builder.append(encode(values));
					}
				} catch (Exception e) {
					throw new IllegalStateException(e.getMessage());
				}
			}
		}
		return builder;
	}

	private boolean doValues(final String key, final String value, final UseType type) {
		if (key == null) {
			throw new NullPointerException("name should not be null");
		}
		switch (type) {
			case APPEND:
				return appendValue(key,value);
			case ADD:
				return appendValue(key,value);
			case REMOVE:
				removeKey(key);
			default:
				return false;
		}
	}

	/**
	 * Returns a List of Keys
	 * <p>
	 * @return a List of String containing the parameters keys, else return  {@code null} if
	 *         the query empty
	 */
	private Set<String> getQueryParameterKeys() {
		return this.uriQueryMap.keySet();
	}

	/**
	 * Clear the query parameter.
	 * <p>
	 *
	 * @return true
	 */
	private boolean clearQuery() {
		this.uriQueryMap.clear();
		return true;
	}

	private Set<Entry<String, List<String>>> entrySet() {
		return this.uriQueryMap.entrySet();
	}

	/**
	 * Returns true if key is find in query map.
	 * <p>
	 * @return <tt>true</tt> - if key is find; <tt>false</tt> - error occurred maybe key not find
	 */
	public boolean containsKey(final String key) {
		return this.uriQueryMap.containsKey(key);
	}

	/**
	 * Get multiple keys from query map
	 * return {@code null} if there are no parameters find in query map
	 * <p>
	 *
	 * @return <tt>true</tt> - List of each name of a parameter; <tt>false</tt> -
	 *       returns an empty Iterator cause keys is empty
	 */
	public Iterator<String> getQueryKeys() {
		return this.uriQueryMap.keySet().iterator();
	}

	/**
	 * Returns a List<String> of parameters return {@code null} if parameters null
	 * <p>
	 * If the parameter has a single value, the List has a size of 1.
	 *
	 * @param key key of parameter to get from query
	 * @return List<String> containing the parameter's values; {@code null}
	 *       if the parameters does not exist
	 */
	public List<String> getQueryParameters(final String key) {
		return this.uriQueryMap.get(key);
	}

	/**
	 * do work with query parameters
	 * <p>
	 *
	 * @param parameters query to be decoded with request object
	 * @param type if request should append or add or reset or remove
	 */
	private void doQueryWork(final CharSequence parameters, UseType type) {
		if (parameters == null || parameters.length() == 0)
			return;

		Set<String> setQuery = null;
		StringTokenizer tokenss = StringToke(parameters.toString());

		while (tokenss.hasMoreTokens()) {
			String token = tokenss.nextToken();
			try {
				String key = valuesQuery(token,true);
				String value = valuesQuery(token,false);

				switch (type) {
					case ADD:
						if (setQuery == null) {
							setQuery = new HashSet<String>();
						}
						if (!setQuery.contains(key)) {
							remove(key);
						}
						setQuery.add(key);
						break;
					case REMOVE:
						removeKey(key);
						break;
					case RESET:
						int index = value.indexOf("?");
						if (index > 0) {
							key = decode(key.substring(index + 10,key.length() - 1));
						}
						break;
					case APPEND:
						break;
				}
				doValues(key,value,UseType.APPEND);
			} catch (Exception e ) {
				throw new IllegalStateException(e.getMessage());
			}
		}
	}

	/**
	 * append if parameters with this key already exist, then it will be remove 
	 * and new one will be created
	 * <p>
	 *
	 * @param key query to be decoded with request object
	 * @param value if request should append or add or reset or remove
	 */
	private boolean appendValue(final String key, final String value) {
		List<String> valueKeys = getQueryParameters(key);
		if (valueKeys != null) {
			removeKey(key);
		}
		//name not exits create one
		List<String> nqueryValues = new ArrayList<String>();
		nqueryValues.add(value);
		this.uriQueryMap.put(key,nqueryValues);
		return true;
	}

	/**
	 * Removes the key from query map.
	 * <p>
	 * check if have multiple key if true remove it all.
	 *
	 * @param key key to remove form query map
	 * @return <tt>true</tt> - if key successful remove; <tt>false</tt> - error occurred maybe key not find
	 */
	private boolean removeKey(String key) {
		return this.uriQueryMap.remove(key) != null;
	}

	private StringTokenizer StringToke(String value) {
		return new StringTokenizer(value,String.valueOf(RAND_NAME) + SEMI_NAME);
	}

	/**
	 * do real work with query parameters
	 * <p>
	 *
	 * @param curi given URI
	 * @param type type should do work only for type request
	 * @return
	 */
	private String doRealQueryWork(String curi,URIValue type) {
		int indexOf = curi.indexOf("/");
		int index = curi.indexOf("?");
		String uri = curi;
		try {
			if (indexOf > 0) {
				switch (type) {
					case GET_AUTHORITY:
						uri = getAuthority();
						break;
					case GET_BEFORE_LAST_PATH_OF_URL:
						uri = getPathBeforeLastPath();
						break;
					case GET_AND_REMOVE_ALL_QUERY:
						uri= removeQuery(uri);
						break;
					case GET_LAST_PATH:
						uri = getLastPath();
						break;
					case GET_PATH:
						uri = getPath();
						break;
					case GET_HOST:
						uri = getHost();
						break;
					case GET_SCHEME:
						uri = getScheme();
						break;
				}
			} else {
				if (index > 0) {
					uri = decode(substring(0, index));
				} else {
					uri = decode(substring(0,-index + -indexOf));
				}
			}
		} catch (Exception e) {
			//throw new IllegalStateException(e.getMessage());
		}
		return uri;
	}

	/**
	 * Get value and key in URI if {@code isKey} true get only key
	 * else get value from URI
	 *
	 * @param uri given URI
	 * @param iskey get only key
	 * @return
	 */
	private String valuesQuery(String uri,boolean iskey){
		int index = uri.indexOf('=');
		if (index == -1) {
			if(iskey) {
				return decode(uri);
			}else {
				return null;
			}
		} else {
			if(iskey) {
				return decode(substring(0, index));
			}else {
				return decode(substring(index + 1));
			}
		}
	}

	/**
	 * Get List of query parameters
	 * {java.util.List<String> String}
	 *
	 * @return {java.util.LinkedHashMap<String, List<String>> LinkedHashMap}
	 */
	public Map<String, List<String>> getUrlQueryMap() {
		LinkedHashMap<String, List<String>> querymap = new LinkedHashMap<String, List<String>>();
		for (Map.Entry<String, List<String>> entry : entrySet()) {
			List<String> listValues = entry.getValue();
			querymap.put(entry.getKey(),new ArrayList<String>(listValues));
		}
		return querymap;
	}

	/**
	 * Get base domain name of URI. E.g. http://www.google.com/support/mobile/ will return
	 * google.com
	 *
	 * @see #getHost()
	 * @return domain name from URI
	 */
	public String getAuthority() {
		String authority = getHost();
		//authority = authority != null ? authority : "";

		int sIndex = 0;
		int nIndex = authority.indexOf(".");
		int lIndex = authority.lastIndexOf(".");
		//check if many nextIndex remove them
		while (nIndex < lIndex) {
			sIndex = nIndex + 1;
			//try to check if indexOf
			nIndex = authority.indexOf(".", sIndex);
		}
		authority = sIndex > 0 ? authority.substring(sIndex) : authority;
		return authority;
	}

	/**
	 * Get Last Extension of URI. E.g. http://www.domain.com/search.php will return
	 * {@code php}
	 *  URI to get last extension from
	 * @return Extension
	 */
	public String getLastExtension() {
		String extension = getPath();
		int gIndex = 0;
		int nIndex = extension.indexOf(".");
		int lIndex = extension.lastIndexOf(".");

		while (nIndex < lIndex) {
			gIndex = nIndex + 1;
			nIndex = extension.indexOf(".", gIndex);
		}
		extension = gIndex > 0 ? decode(extension.substring(gIndex)) : extension.indexOf(".") > 0 ? extension : "";
		return extension.indexOf(".") > 0 ? decode(extension.substring(extension.indexOf(".")+ 1)) : extension;
	}

	/**
	 * Get Extension of URI. E.g. http://www.google.com will return
	 * {@code com}
	 *  URI to get extension from
	 * @return Extension
	 */
	public String getExtension() {
		String extension = getAuthority();
		int startindex = extension.indexOf(".");
		if (startindex > 0) {
			extension = decode(extension.substring(startindex+ 1));
		}
		return extension;
	}

	/**
	 * Get only scheme part("scheme://") type
	 *
	 * @see #getScheme()
	 * @return {com.freedom.asyncimageloader.uri.URLEncoded.UriScheme UriScheme}
	 */
	public UriScheme getSchemeType() {
		return UriScheme.match(uri);
	}

	/**
	 * Get only scheme part("scheme://") of URL. E.g. http://www.google.com will return
	 * {@code ("http")}
	 *
	 * @return scheme
	 */
	public String getScheme() {
		int indexOf = uri.indexOf(":");
		String scheme = indexOf > 0 ? decode(substring(0, indexOf)) : "";
		return scheme;
	}

	/**
	 * Get only scheme specific part("//") of URI. E.g. http://www.google.com will return
	 * {@code ("//www.google.com")}
	 *
	 * @return scheme part("//")
	 */
	public String getSchemeSpecificPath() {
		int indexOf = uri.indexOf("/");
		String schemeSPart = indexOf > 0 ? decode(substring(indexOf + 0)) : "";
		String xx = "?";
		return schemeSPart+xx+getQuery();
	}

	/**
	 * Get only the part of URI. E.g. http://www.google.com/support/mobile/ will return
	 * {@code path: /support/mobile/}
	 *
	 * @return {@code path}
	 */
	public String getPath() {
		String path = removeQuery(removeScheme());
		int index = path.indexOf("/");
		if (index > 0) {
			path = decode(path.substring(index + 0));
		} else {
			//check if next index is low than last index if true make path null
			//else if next index is more than last index make path path 
			path = path.indexOf(".") < path.lastIndexOf(".") ? "" : path;
		}
		return path;
	}

	private String getSlashPath() {
		String path = getPath();
		return !path.equals("") && !path.startsWith("/") ? "/"+path : path;
	}

	public List<String> getPathSegments() {
		String pathList = getSlashPath();
		List<String> paths = new ArrayList<String>();
		int gIndex = 0;
		int nIndex = pathList.indexOf("/");
		String[] parts = pathList.split("/");
		int lIndex = pathList.lastIndexOf("/");
		int sIndex = parts.length;

		while (nIndex < lIndex) {
			gIndex = nIndex + 1;
			sIndex = sIndex- 1;
			paths.add(decode(parts[parts.length - sIndex]));
			nIndex = pathList.indexOf("/", gIndex);
		}
		return paths;
	}

	/**
	 * Get Before Last Part of URL. E.g. http://www.google.com/support/mobile/ will return
	 * {@code support}
	 *
	 * @return {@code path}
	 */
	public String getPathBeforeLastPath() {
		String beforeLast = getSlashPath();
		String[] parts = beforeLast.split("/");
		beforeLast = parts.length > 1 ? decode(parts[parts.length - 2]) : "";
		return beforeLast;
	}

	/**
	 * Get Last path of URL. E.g. http://www.google.com/support/mobile/ will return
	 * {@code mobile}
	 *
	 * @return {@code path}
	 */
	public String getLastPath() {
		String[] parts = getSlashPath().split("/");
		String lastPart = parts.length > 0 ? decode(parts[parts.length - 1]) : "";
		return lastPart;
	}

	/**
	 * Get Last path of URL without extension. E.g. http://domain.com/search.php will return
	 * {@code search}
	 *
	 * @return {@code path}
	 */
	public String getLastPathWithoutExtension() {
		String uri = getLastPath();
		int indexOf = uri.indexOf(".");
		uri = indexOf > 0 ? decode(substring(0, indexOf)) : "";
		return uri;
	}

	/**
	 * Get path from uri without query by using getPathWithoutQuery()
	 *
	 * @return uri
	 */
	public String getPathWithoutQuery() {
		// TODO: Implement this method
		return "";
	}

	/**
	 * Get slash path from uri without query by using getSlashPathWithoutQuery()
	 *   URI is a parseWord(word)
	 * @return uri
	 */
	public String getSlashPathWithoutQuery() {
		// TODO: Implement this method
		return "";
	}

	/**
	 * Get path from uri without last path by using getPathWithoutLastPath()
	 *  URI is a parseWord(word)
	 * @return uri
	 */
	public String getPathWithoutLastPath() {
		// TODO: Implement this method
		return "";
	}

	/**
	 * Remove only scheme part("scheme://") from URI. this finally remove scheme from URL
	 *
	 * <p>This method can only be use when URIMaster is still valid.</p>
	 * If you try to get scheme from URI with same URIMaster it will return null
	 *
	 * @see #removeScheme()
	 */
	public void removeSchemeFromUri() {
		this.uri = removeScheme();
	}

	/**
	 * Remove all query from URI. this finally remove query from URL
	 *
	 * <p>This method can only be use when URIMaster is still valid.</p>
	 * If you try to get query from URI with same URIMaster it will return only the URL
	 *
	 * @see #clearQuery()
	 * @see #removeQuery()
	 */
	public void removeQueryFromUri() {
		clearQuery();
	}

	/**
	 * Remove only scheme part("scheme://") from URI. E.g. http://www.google.com will return
	 * google.com
	 *
	 * @return URI
	 */
	public String removeScheme() {
		onStartWork(true);
		int index = uri.indexOf("//");
		String uri = this.uri;
		if (index > 0) {
			uri = decode(substring(index + 2));
		} else {
			uri = decode(uri);
		}
		onStartWork(false);
		return workUri(uri);
	}

	/**
	 * Remove all query from URI. E.g. http://www.google.com/search?q=lovely will return
	 * http://www.google.com/search
	 *
	 * @return URI
	 */
	public String removeQuery() {
		return removeQuery("?",true);
	}

	/**
	 * Remove query from uri by using removeQuery(uri,query,isStart)
	 *   URI is a parseWord(word)
	 * @param query query to be remove
	 * @param isStart set maybe to start from the first path or last path
	 * @return (true,false)
	 */
	public String removeQuery(String query,boolean isStart) {
		return removeQuery(uri,query,isStart);
	}

	/**
	 * Remove query from uri by using removeQuery(uri,query,isStart)
	 *
	 * @param uri URI is a parseWord(word)
	 * @param query query to be remove
	 * @param isStart set maybe to start from the first path or last path
	 * @return (true,false)
	 */
	public String removeQuery(String uri,String query,boolean isStart) {
		onStartWork(true);
		int index = uri.indexOf(query);
		if (isStart) {
			if (index > 0) {
				uri = decode(substring(uri,0, index));
			}
		} else {
			if (index > 0) {
				uri = decode(substring(uri,index+ 1));
			}
		}
		onStartWork(false);
		return workUri(uri);
	}

	/**
	 * Check if word match the first path and the last path by using hasStartOrEnd(isStart,word)
	 *  URI is a parseWord(word)
	 * @param start word to search if match the first path
	 * @param end word to search if match the last path
	 * @return (true,false)
	 */
	public boolean hasStartAndEnd(String start,String end) {
		if (uri == null)
			return false;
		boolean cUriWork = cUriOnWork;
		this.cUriOnWork = false;
		if (start.contains("-") && end.contains("-")) {
			onStartWork(true);
			String s = removeQuery(start,"-",true);
			String s2 = removeQuery(start,"-",false);
			String e = removeQuery(end,"-",true);
			String e2 = removeQuery(end,"-",false);
			this.cUriOnWork = cUriWork;
			onStartWork(false);
			return (startsWith(s) || startsWith(s2)) && (endsWith(e) || startsWith(e2));
		}
		if (start.contains("-") ) {
			return hasStartOrEnd(true,start);
		}
		if (end.contains("-")) {
			return hasStartOrEnd(false,end);
		}

		if (startsWith(start) && endsWith(end)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if word match the first or last path by using hasStartOrEnd(isStart,word)
	 *  URI is a parseWord(word)
	 * @param isStart means you can set to false to check for the last path
	 * @param word search if the word match the first path and return true
	 * @return (true,false)
	 */
	public boolean hasStartOrEnd(boolean isStart,String word) {
		onStartWork(true);
		boolean cUriWork = cUriOnWork;
		boolean isWorkDone = false;
		if (isStart) {
			if (word.contains("-")) {
				this.cUriOnWork = false;
				String sStart = removeQuery(word,"-",true);
				String sLast = removeQuery(word,"-",false);
				this.cUriOnWork = cUriWork;
				isWorkDone = (startsWith(sStart) || startsWith(sLast));
			} else {
				if (startsWith(word))
					isWorkDone = true;
			}
		} else {
			if (word.contains("-")) {
				this.cUriOnWork = false;
				String sStart = removeQuery(word,"-",true);
				String sLast = removeQuery(word,"-",false);
				this.cUriOnWork = cUriWork;
				isWorkDone = (endsWith(sStart) || endsWith(sLast));
			} else {
				if (endsWith(word))
					isWorkDone = true;
			}
		}
		onStartWork(false);
		return isWorkDone;
	}

	public boolean hasLength() {
		onStartWork(true);
		if (uri != null) {
			for (int i = 0; i < uri.length(); i++) {
				return true;
			}
		}
		onStartWork(false);
		return false;
	}

	public boolean hasWhiteSpace() {
		return getWhiteSpaceList().size() != 0;
	}

    public List<String> getWhiteSpaceList() {
        return getWhiteSpaceList(false);
    }

    public List<String> getWhiteSpaceList(boolean isNatural) {
        return checkUriGetLineList(uri,null, isNatural);
    }

	public boolean isEmpty(int s) {
		if (s == 0)
			return true;
		else
			return false;
	}

	public boolean isEmpty(String s) {
		if (s == null)
			return true;
		if (s.equals("") || s.length() == 0)
			return true;
		return false;
	}

	public List<String> checkUriGetLineList(String check, boolean isOnlyNatural) {
		return checkUriGetLineList(uri, check, isOnlyNatural);
	}

	private List<String> checkUriGetLineList(String uri, String check, boolean isOnlyNatural) {
		onStartWork(true);

		List<String> spaces_list = new ArrayList<String>();
		if (isOnlyNatural)  {
		    String[] words = uri.split(" ");
		    spaces_list.addAll(Arrays.asList(words));
		} else {
			if (uri != null) {
			    String ss = "";
				for (int i = 0; i < uri.length(); i++) {
					String schar = uri.substring(i,i+1);

					if ((check != null && !check.isEmpty() && check.equals(schar))
							|| Character.isWhitespace(uri.charAt(i))) {
						spaces_list.add(ss);
						ss = "";
					} else {
						ss = ss + schar;
					}
					if (i == uri.length() - 1) {
						spaces_list.add(ss);
					}
				}
			}
		}
		onStartWork(false);
		return spaces_list;
	}

	public List<String> readLine(boolean isCount,String replace)  {
		return readLine(isCount,0,replace);
	}

	public List<String> readLine(boolean isCount,int line,String replace)  {
		return readLine(isCount,line,0,0,replace);
	}

	public List<String> readLine(boolean isCount,int line,int lineStart,int lineEnd,String replace)  {
		String uri = this.uri;
		if (uri == null || uri.length() == 0)
			return null;
		onStartWork(true);
		List<String> count = new ArrayList<>();
		String sCount = "",msg = "";
		int linesCount = 0;
		int lineOnStart = 0;
		boolean isPassFirst = false;
		boolean isReplace = false;
		boolean hasStart = false;
		clearRemoveCount();
		boolean isAddNum = isCount;

		for (int pos = 0; pos < uri.length(); pos++) {
			char c = uri.charAt(pos);
			boolean isOnLineSet = linesCount == line;
			if (isOnLineSet)   lineOnStart++;

			if ( !isPassFirst) {
				linesCount++; isPassFirst = true;
				if (isOnLineSet)
					lineOnStart++;

				if (isCount)
					putOrUpdateRemoveCount(false,linesCount,pos);
				msg = isAddNum ? msg+linesCount :
						isEmpty(line) ? msg+replace : msg;
			}
			if(c == '\r' || c== '\n') {
				linesCount++;
				if (replace.length() != 0) {
					if (count.size() == line && msg.endsWith(Separator))
						msg = msg+replace;
					if (isAddNum)
						msg = msg+c+linesCount;
					else if (isEmpty(line))
						msg = msg+replace;
					else
						msg = msg+c;
				}

				if (isCount)
					putOrUpdateRemoveCount(false,linesCount,pos);
				count.add(sCount); sCount = "";
			} else {
				sCount = sCount+c;
				if (isOnLineSet) {
					if ( isReplace && lineEnd != 0)
						msg = msg+c;
					else if ( lineStart != 0) {
						if ( hasStart) {

						} else  if (lineStart == lineOnStart) {
							hasStart = true;
						} else {
							msg = msg+c;
						}
					} else if (checkUriGetLineList(c+"",null,true).size() != 0)
						msg = msg+c;

					if ((! (checkUriGetLineList(c+"", null, true).size() != 0) && !isReplace && lineEnd == 0) || (!isReplace && lineEnd != 0 && lineOnStart == lineEnd) ) {
						msg = msg+replace; isReplace = true;	hasStart = false;
					}
				} else {
					msg = msg+c;
				}
			}

			if (pos+1 == uri.length()) {
				count.add(sCount); sCount = "";
			}
		}
		count.add(uri);
		putOrUpdateRemoveCount(Separator,linesCount+"");
		putOrUpdateRemoveCount(Uri_Word,msg);
		onStartWork(false);
		return count;
	}

	public List<String> removeLine(String replace) {
		return removeLine(replace,false);
	}

	public List<String> removeLine(String replace,boolean isCount) {
		onStartWork(true);
		String uri = this.uri;
		String word = uri;
		List<String> count = null;
		if (isCount) {
			count = readLine(isCount,replace);
		}
		onStartWork(false);
		workUri(word);
		return count;
	}

	public String replaceLine(int line,String replace) {
		return replaceLine(line,0,0,replace);
	}

	public String replaceLine(int line,int start,String replace) {
		return replaceLine(line,start,0,replace);
	}

	public String replaceLine(int line,int start,int end,String replace) {
		readLine(false,line,start,end,replace);
		String word = getRemoveCount(Uri_Word);
		onStartWork(false);
		return workUri(word);
	}

	/**
	 * Remove all space from given uri by using removeSpaces()
	 *  URI is a parseWord(word)
	 * @return uri
	 */
	public String removeSpaces() {
		onStartWork(true);
		if (!hasLength())
			return this.uri;

		String word = removeWord(new String[]{" ", "\n\n\\s+"}, "", true);
		onStartWork(false);
		return workUri(word);
	}

	/**
	 * First path to be remove from given uri by using ifFirstWordRemove(new String[]{"word"})
	 *  URI is a parseWord(word)
	 * @param s means you can make a list of word 
	if one of those word match the first path it will remove it and return the remaining path
	 * @return uri
	 */
	public String ifFirstWordRemove(String[] s) {
		if (uri == null || s == null || s.length == 0)
			return uri;
		for (int i=0;i<s.length;i++) {
			String remove = s[i];
			if (uri.startsWith(remove)) {
				return workUri(removeFirstWord(remove));
			}
		}
		return workUri(uri);
	}

	/**
	 * Get given uri by using getWord()
	 *     startTime time is the time parse by onStart()
	 * @return uri
	 */
	public String getLastWorkTime() {
		if (startDate != 0)
			return TimeUtils.parse(startDate, endDate).getRealTime();
		return "no work been done yet";
	}

	/**
	 * Get given uri by using getWord()
	 *  URI is a parseWord(word)
	 * @return uri
	 */
	public String getWord() {
		return uri;
	}

	/**
	 * Get the count that a letter is been remove inside a string parse by parseWord("good god gat")
	 * (E.eg) parseWord("good god gat").removeWord("g",true) g will be remove from 3 line
	 *         that will return 3 count when getting the getRemoveCount(target)
	 *  URI is a parseWord(word)
	 * @return (count)
	 */
	public String getRemoveCount(String target) {
		onStartWork(true);
		String count = "";
		if (removeCount.containsKey(target))
			count =  removeCount.get(target);
		else
			count = "";
		onStartWork(false);
		return count;
	}

	public Map<String, String> getRemoveCount() {
		return removeCount;
	}

	public boolean clearRemoveCount() {
		if (removeCount != null && removeCount.size() != 0)
			removeCount.clear();
		return true;
	}

	private boolean putOrUpdateRemoveCount(int s,int count) {
		return putOrUpdateRemoveCount(true,s,count);
	}

	private boolean putOrUpdateRemoveCount(boolean isUpdate,int s,int count) {
		return putOrUpdateRemoveCount(String.valueOf(s),String.valueOf(count));
	}

	private boolean putOrUpdateRemoveCount(String s,String count) {
		return putOrUpdateRemoveCount(true,s,count);
	}

	private boolean putOrUpdateRemoveCount(boolean isUpdate,String s,String count) {
		if (isUpdate && removeCount.containsKey(s)) {
			count = getRemoveCount(s) + count;
		}
		removeCount.put(s,count);
		return true;
	}

	/**
	 * Get first word from given uri by using getFirstWord()
	 *  URI is a parseWord(word)
	 * @return uri
	 */
	public String getFirstWord() {
		return getFirstWord(null);
	}

	/**
	 * Get first word from given uri by using getFirstWord(new String[]{"word"})
	 *  URI is a parseWord(word)
	 * @param isIndex means you can make a list of word and see if one of the word match the first word
	 * @return uri
	 */
	public String getFirstWord(String[] isIndex) {
		onStartWork(true);
		String uri = this.uri;
		if (isIndex != null && isIndex.length != 0) {

		} else {
			if (uri == null || uri.equals(""))
				uri = this.uri;
		}
		if (isIndex != null && isIndex.length != 0) {
			for (int is = 0; is < isIndex.length; is++) {
				int i = uri.indexOf(isIndex[is]);
				if (i > 0) {
					onStartWork(false);
					return substring(0, i);
				}
			}
		} else {
			int i = uri.indexOf(' ');
			if (i > 0)
				uri = substring(0, i);
		}
		onStartWork(false);
		return uri;
	}

	/**
	 * Get last word from given uri by using getLastWord()
	 *  URI is a parseWord(word)
	 * @return uri
	 */
	public String getLastWord() {
		return getLastWord(null);
	}

	/**
	 * Get last word from given uri by using getLastWord(new String[]{"word"})
	 *  URI is a parseWord(word)
	 * @param isIndex means you can make a list of word and see if one of the word match the last word
	 * @return uri
	 */
	public String getLastWord(String[] isIndex) {
		onStartWork(true);
		String uri = this.uri;
		if (isIndex != null && isIndex.length != 0) {

		} else {
			if (uri == null || uri.equals("") || uri.indexOf(' ') == 0)
				return uri;
		}

		if (isIndex != null && isIndex.length != 0) {
			for (int is = 0; is < isIndex.length; is++) {
				int i = uri.lastIndexOf(isIndex[is]);
				if (i > 0) {
					onStartWork(false);
					return substring(i, uri.length());
				}
			}
		} else {
			String[] arr = uri.split("\\s+");
			if (arr.length > 0)
				uri = arr[arr.length -1];
			//String rest = substring(i);
		}
		onStartWork(false);
		return uri;
	}

	/**
	 * Remove first word from given uri by using removeFirstWord()
	 *
	 * @return uri
	 */
	public String removeFirstWord() {
		return removeFirstWord(getFirstWord());
	}

	/**
	 * Remove last word from given uri by using removeLastWord()
	 *
	 * @return uri
	 */
	public String removeLastWord() {
		return removeLastWord(getLastWord());
	}

	/**
	 * Remove first word from given uri by using removeFirstWord(eWith)
	 *
	 * @param sWith means you can also specific first path to remove
	 * @return uri
	 */
	public String removeFirstWord(String sWith) {
		onStartWork(true);
		String uri = this.uri;
		if (uri == null || equals(sWith) || !startsWith(sWith) || uri.indexOf(' ') == 0)
			uri = this.uri;
		else
			uri = uri.indexOf(' ') > 0 ? substring(sWith.length()+1) : substring(sWith.length());
		onStartWork(false);
		return workUri(uri);
	}

	/**
	 * Remove last word from given uri by using removeLastWord(eWith)
	 *
	 * @param eWith means you can also specific last path to remove
	 * @return uri
	 */
	public String removeLastWord(String eWith) {
		onStartWork(true);
		String uri = this.uri;
		if (uri == null || equals(eWith) || !endsWith(eWith) || uri.indexOf(' ') == 0)
			return uri;
		if (uri.length() > 0 && eWith.length() > 0) {
			uri = substring(0, uri.length()- eWith.length());
		} else {
			uri = substring(0,uri.length()-1);
		}
		onStartWork(false);
		return workUri(uri);
	}

	/**
	 * Remove"" a single letter "y" from "johnny" and return "johnn" by using removeWord(word)
	 * you can also replace a single letter by using removeWord(word,replace)
	 *
	 * @param word is a letter which need to be remove
	 * @return uri
	 */
	public String removeWord(String word) {
		return removeWord(word,false);
	}

	/**
	 * Remove"" a single letter "y" from "johnny" and return "johnn" by using removeWord(word,isRemoveAll)
	 * you can also replace a single letter by using removeWord(word,replace)
	 *
	 * @param word is a letter which need to be remove or replace
	 * @param isRemoveAll if you enable this it will allow {@Param replace} to remove or replace every path of the word that contains (@Param word)
	 * @return uri
	 */
	public String removeWord(String word,boolean isRemoveAll) {
		return removeWord(word,"",isRemoveAll);
	}

	/**
	 * Remove"" or replace"s" a single letter "y" from "johnny" and return "johnn" or "johnns" by using removeWord(word,replace)
	 * you can also replace a single letter by using removeWord(words,replace,isRemoveAll)
	 *
	 * @param word is a letter which need to be remove or replace
	 * @param replace is a word that will replace any match letter containing  {@Param word}
	 * @return uri
	 */
	public String removeWord(String word,String replace) {
		return removeWord(word,replace,false);
	}

	/**
	 * Remove"" or replace"s" a single letter "y" from "johnny" and return "johnn" or "johnns" by using removeWord(word,replace,isRemoveAll)
	 * you can also replace a single letter by using removeWord(words,replace,isRemoveAll)
	 *
	 * @param word is a letter which need to be remove or replace
	 * @param replace is a word that will replace any match letter containing  {@Param word}
	 * @param isRemoveAll if you enable this it will allow {@Param replace} to remove or replace every path of the word that contains (@Param words)
	 * @return uri
	 */
	public String removeWord(String word, String replace, boolean isRemoveAll) {
		return removeWord(new String[]{word},replace,isRemoveAll);
	}

	/**
	 * Remove"" or replace"s" a single letter "y" from "johnny" and return "johnn" or "johnns" by using removeWord(words,replace)
	 * you can also replace a single letter by using removeWord(words,replace)
	 *
	 * @param words is a array that contain many string which need to be remove or replace
	 * @param replace is a letter that will replace any match letter containing  {@Param words}
	 * @return uri
	 */
	public String removeWord(String[] words,String replace) {
		return removeWord(words,replace,false);
	}

	/**
	 * Remove"" or replace"s" a single letter "y" from "johnny" and return "johnn" or "johnns" by using removeWord(words,isRemoveAll)
	 * you can also replace a single letter by using removeWord(words,replace,isRemoveAll)
	 *
	 * @param words is a array that contain many string which need to be remove or replace
	 * @param isRemoveAll if you enable this it will allow {@param ""} to remove or replace every path of the word that contains (@Param words)
	 * @return uri
	 */
	public String removeWord(String[] words,boolean isRemoveAll) {
		return removeWord(words,"",isRemoveAll);
	}

	/**
	 * Remove"" or replace"s" a single letter "y" from "johnny" and return "johnn" or "johnns" by using removeWord(words,replace,isRemoveAll)
	 * you can also replace a single letter by using removeWord(words,replace,isRemoveAll)
	 *
	 * @param words is a array that contain many string which need to be remove or replace
	 * @param replace is a letter that will replace any match letter containing  {@Param words}
	 * @param isRemoveAll if you enable this it will allow {@param replace} to remove or replace every path of the word that contains (@Param words)
	 * @return uri
	 */
	public String removeWord(String[] words,String replace,boolean isRemoveAll) {
		onStartWork(true);
		if (uri == null || equals(words[0]))
			return uri;

		String newUri = uri;
		for (String word : words) {
			//newUri = newUri +" "+ word;
			newUri = removeChar(newUri, word, replace, isRemoveAll);
		}
		onStartWork(false);
		return workUri(newUri);
	}

	public String removeDoubleQuotes() {
		return removeDoubleQuotes(false);
	}

	public String removeDoubleQuotes(boolean isRemoveAll) {
		if (uri.contains(DoubleQuote)) {
			uri = removeWord(DoubleQuote,"",isRemoveAll);
		}
		return workUri(uri);
	}

	private String removeChar(String uri, int target,String replace,boolean isRemoveAll) {
		return removeChar(uri,target,replace,isRemoveAll);
	}

	private String removeChar(String uri, String target,boolean isRemoveAll) {
		return removeChar(uri,target,"",isRemoveAll);
	}

	private String removeChar(String uri, Object target, String replace, boolean isRemoveAll) {
		int indexOf = 0;
		int length = 0;
		if (target instanceof Integer) {
			indexOf = (Integer) target;
			length = uri.indexOf(indexOf+1) > 0 ? 1 : 0;
		} else {
			indexOf = uri.indexOf(target.toString());
			length = target.toString().length();
		}
		if(indexOf ==-1)
			return uri;

		String first = uri.substring(0, indexOf);
		first = replace.equals("") ? first : first+replace;
		String end = uri.substring(indexOf+length, uri.length());

		if (replace.equals("") && uri.substring(indexOf, indexOf+length).equals(target)) {
			if (first.endsWith(" ") && end.startsWith(" "))
				end = end.substring(1, end.length());
		}
		putOrUpdateRemoveCount(target.toString(),"1");

		String newUri = first+end;
		if (isRemoveAll) {
			if (target instanceof Integer) {
				if (newUri.indexOf((Integer)target) ==-1) {
					return newUri;
				} else {
					return removeChar(newUri,target,replace, true);
				}
			} else {
				if (!newUri.contains(target.toString())) {
					return newUri;
				} else {
					return removeChar(newUri,target,replace, true);
				}
			}
		}
		return workUri(newUri);
	}

	/**
	 * Change the given URI such as http://www.stackoverflow.com and return
	 * www.stackoverflow.com
	 *
	 * @return domain URI
	 */
	public String getHost() {
		if(uri == null || uri.length() == 0)
			return "";
		int slash = uri.indexOf("//");
		if(slash == -1)
			slash = 0;
		else
			slash += 2;
		int end = uri.indexOf('/', slash);
		end = end >= 0 ? end : uri.length();
		int port = uri.indexOf(':', slash);
		end = (port > 0 && port < end) ? port : end;
		String host = substring(slash, end);
		return host.indexOf('.') > 0 ? host : "";
	}

	/**
	 * Get all query from URI. E.g. http://www.google.com/search?q=lovely will return
	 * q=lovely
	 *
	 * @param uri given URI
	 * @return query
	 */
	private static String retrieveQuery(String uri) {
		int index = uri.indexOf("?");
		String query;
		if (index > 0) {
			query = decode(uri.substring(index + 1));
		} else {
			query = decode(uri);
		}
		return query;
	}

	/**
	 * Encode URI value
	 *
	 * @param value
	 * @return encoded value
	 */
	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Decode URI value
	 *
	 * @param value
	 * @return decoded value
	 */
	public static String decode(String value) {
		try {
			return URLDecoder.decode(value, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	private String removeQuery(String uri) {
		int index = uri.indexOf("?");
		uri = index > 0 ? decode(substring(0, index)) : uri;
		return uri;
	}

	private String workUri(String uri) {
		if (cUriOnWork) {
			this.uri = uri;
		}
		return uri;
	}

	private boolean endsWith(String word) {
		return uri.endsWith(word.toLowerCase());
	}

	private boolean startsWith(String word) {
		return uri.startsWith(word.toLowerCase());
	}

	private boolean equals(String word) {
		return uri.equals(word.toLowerCase());
	}

	private boolean contains(String word) {
		return uri.contains(word.toLowerCase());
	}

	public String replace(CharSequence target, CharSequence replacement) {
		return removeWord(target+"", replacement+"",false);
	}

	public String replaceAll(String regularExpression, String replacement) {
		return removeWord(regularExpression, replacement,true);
	}

	public String substring(int start) {
		return substring(uri,start);
	}

	public String substring(String uri, int start) {
		return substring(uri,start,0);
	}

	public String substring(int start,int end) {
		return substring(uri,start,end);
	}

	boolean isUriString = false;
	private String substring(String uri, int start, int end) {
		if (isUriString) {
			String oldText = "";
			String newText = "";
			boolean hasStart = false,hasEnd = false;
			int length = 0;
			if ( start == 0)
				hasStart = true;
			/*if ( end == 0)
				hasEnd = true;*/
			for (int pos = 0; pos < uri.length(); pos++) {
				length++;
				char c = uri.charAt(pos);

				if (hasStart && hasEnd) {
					if ( newText.length() == 0)
						return newText+c;
					else
						return newText;
				}
				if (start == length || hasStart) {
					hasStart = true;
					newText = newText+c;
				}

				if(end == length) {
					hasEnd = true;
					return newText;
				} else {
					oldText = oldText+c;
				}
			}
			return newText;
		} else {
			return end == 0 ? uri.substring(start) : uri.substring(start,end);
		}
	}

	private char charAt(int at) {
	/*	if (isUriString) {	
			String newText = "";
			int length = 0;
			char[] array = uri.toCharArray();
			for (char c : array) {
			       length++;			   
				   if (length == at) 
					    return c;
		     }
			 return 0;
	     } else {*/
		return uri.charAt(at);
		// }
	}

	private final Map<String, List<String>> uriQueryMap = new LinkedHashMap<String, List<String>>();
	private String uri = null;
	private static String CHARSET_NAME = "UTF-8";

	public enum QuerySeparators {
		QUERY,
		SEMI,
		RAND,
		OTHER
	};

	enum QueryType {
		SINGLE,
		MULTIPLE
	};

	enum UseType {
		ADD,
		REMOVE,
		APPEND,
		RESET
	};

	public enum URIValue {
		GET_AUTHORITY,
		GET_BEFORE_LAST_PATH_OF_URL,
		GET_AND_REMOVE_ALL_QUERY,
		GET_LAST_PATH,
		GET_PATH,
		GET_HOST,
		GET_SCHEME,
	};

	public interface SchemeCallback {
		void onSchemeFind(UriScheme scheme,String schemeString);
		void onException(Throwable e);
	};

	/** This is custom scheme of URI. Allow many methods of schemes for URL. */
	public enum UriScheme {
		/** Network scheme. */
		HTTP("http"), HTTPS("https"),
		/** Content and raw scheme. */
		CONTENT("content"), RAW("raw"),
		/** Drawable and assets scheme. */
		DRAWABLE("drawable"),ASSETS("assets"),
		/** File and thumb scheme. */
		FILE("file"),THUMB("thumb"), UNKNOWN_URI("");

		private String schemeType = null;
		private String type;
		private String withPrefix = "://";

		/** Place prefix on incoming scheme of URL. */
		private UriScheme(String uriType) {
			this.type = uriType;
			this.schemeType = type + withPrefix;
		}

		/**
		 * Get Scheme from given URL
		 * Get list of scheme value in {com.freedom.asyncimageloader.uri.URLEncoded.UriScheme UriScheme}
		 *
		 * If URL scheme not find return {com.freedom.asyncimageloader.uri.uri.URLEncoded.UriScheme.UNKNOWN_URI UNKNOWN_URI}
		 *
		 * @param uri URL which contains define scheme
		 * @return URLScheme
		 */
		public static UriScheme match(String uri) {
			if (uri != null) {
				for (UriScheme usm : values()) {
					if (uri.startsWith(usm.schemeType)) {
						return usm;
					}
				}}
			return UNKNOWN_URI;
		}

		/**
		 * Get Scheme from given URL and return scheme using callback
		 * @see #match(String)
		 *
		 * @param uri URL which contains define scheme
		 * @param callback which will be use to return URL scheme
		 */
		public static void match(String uri,SchemeCallback callback) {
			UriScheme scheme = match(uri);
			if(scheme != UNKNOWN_URI) {
				callback.onSchemeFind(scheme,scheme.toString());
				return ;
			}
			callback.onException(new Exception("URI scheme not find"));
		}

		/**
		 * Drag scheme part("scheme://") to URL. E.g. www.google.com will return
		 * http://www.google.com
		 *
		 * @param uri URL which scheme will be added to
		 * @return URLScheme
		 */
		public String drag(String uri) {
			return schemeType + uri;
		}

		/**
		 * Drag scheme part("scheme://") to URL. using the given scheme check given scheme if end with slash because
		 * given scheme might only contain E.g. ("http") instead of ("http://")
		 *
		 * @param scheme scheme given scheme to add to URL
		 * @param uri URL which scheme will be added to
		 * @return URLScheme
		 */
		public String drag(String scheme,String uri) {
			if(!scheme.endsWith("/")) {
				//URI only contain scheme with no prefix
				scheme += withPrefix;
			}
			return scheme + uri;
		}

		/**
		 * Remove only scheme part("scheme://") from URL E.g. http://www.google.com will return
		 * www.google.com
		 *
		 * If current scheme does not match URL scheme
		 * return URL
		 *
		 * @param uri URL which current scheme will be removed from
		 * @return URL
		 */
		public String remove(String uri) {
			if (uri == null) {
				//URI null return URI
				return uri;
			}
			if (!uri.startsWith(schemeType)) {
				//URI does not start with current scheme return URI
				return uri;
			}
			return uri.substring(schemeType.length());
		}

		/**
		 * Get scheme string using {toString()}
		 *
		 * <p>This method can only be call only when you sure you have valid {UriScheme}.
		 *  - else this might throw IllegalStateException
		 * </p>
		 * @return Scheme string
		 */
		@Override
		public String toString() {
			switch (this) {
				case HTTP:
					return "http";
				case HTTPS:
					return "https";
				case FILE:
					return "file";
				case CONTENT:
					return "content";
				case ASSETS:
					return "assets";
				case RAW:
					return "raw";
				case THUMB:
					return "thumb";
				case DRAWABLE:
					return "drawable";
				case UNKNOWN_URI:
					return "unknown uri";
				default:
					throw new IllegalStateException("Unknown uri: ");
			}
		}
	}
}
