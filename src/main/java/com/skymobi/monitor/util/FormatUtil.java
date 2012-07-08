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
package com.skymobi.monitor.util;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Hill.Hu
 */

public class FormatUtil {

    public String toJson(Object obj) {

        return new Gson().toJson(obj);
    }

    public String join(Iterator iterator) {
        return StringUtils.join(iterator, ",");
    }

    public String join(List list) {
        return StringUtils.join(list.toArray(), ",");
    }
}
