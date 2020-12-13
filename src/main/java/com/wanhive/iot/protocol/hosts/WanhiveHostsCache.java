/*
 * WanhiveHostsCache.java
 * 
 * Hosts cache implementation
 * 
 * This program is part of Wanhive IoT Platform.
 * 
 * Apache-2.0 License
 * Copyright 2020 Wanhive Systems Private Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.wanhive.iot.protocol.hosts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * The reference implementation of hosts cache
 * 
 * @author amit
 *
 */
public class WanhiveHostsCache implements HostsCache {

	@Override
	public long[] get(String path, int count) throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File(path))) {
			long[] list = new long[count];
			int i = 0;
			while (scanner.hasNextLong() && i < count) {
				list[i++] = scanner.nextLong();
			}

			if (i < count) {
				list = Arrays.copyOfRange(list, 0, i);
			}

			Random rand = new Random();
			for (int x = (list.length - 1); x > 0; --x) {
				int j = rand.nextInt(list.length);
				long tmp = list[j];
				list[j] = list[x];
				list[x] = tmp;
			}

			return list;
		}
	}

}
