/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.model;

import junit.framework.TestCase;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author Hill.Hu
 */
public class LogQueryTest extends TestCase {
    LogQuery query;

    @Override
    public void setUp() throws Exception {
        query = new LogQuery();
    }

    public void test_query() throws Exception {

        assertEquals("{ }", query.toQuery().toString());
        query.setStart("2012-06-08 10:00:00");

        assertEquals("{ \"timestamp\" : { \"$gt\" : { \"$date\" : \"2012-06-08T02:00:00.000Z\"}}}", query.toQuery().toString());
        query.setEnd("2012-06-08 11:00:00");
        query.setLevel("ERROR");
        query.setKeyWord("111");
        System.out.println(query.toQuery().toString());

        Query bquery = new BasicQuery(query.toQuery());
        bquery.limit(100);

        bquery.sort().on("$timestamp", Order.DESCENDING);
        System.out.println(bquery.getQueryObject());
        System.out.println(bquery.getFieldsObject());
        System.out.println(bquery.getSortObject());
//        assertEquals("{ \"timestamp\" : { \"$gt\" : { \"$date\" : \"1970-01-01T00:01:40.000Z\"}} , \"$where\" : \"this.message && this.message.match('hello')\"}",query.toQuery().toString());
    }


}
