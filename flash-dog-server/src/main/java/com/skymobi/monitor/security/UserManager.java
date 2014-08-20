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
package com.skymobi.monitor.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
* @author hill.hu
 *
 */
public interface UserManager {
    List<User> listUsers();

     void registerUser(User user)    ;

    public User loadUserByUsername(String userName);

    List<String> loadAdmins();

    boolean isSystemAdmin(String username);

    void monitorUser(User user);

    void removeUser(String username);
}
