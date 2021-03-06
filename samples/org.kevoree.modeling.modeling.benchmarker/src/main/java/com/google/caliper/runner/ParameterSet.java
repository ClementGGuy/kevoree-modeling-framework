/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * Fouquet Francois
 * Nain Gregory
 */
package com.google.caliper.runner;

import com.google.caliper.api.Benchmark;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Represents all the injectable parameter fields of a single kind (@Param or @VmParam) found in a
 * benchmark class. Has nothing to do with particular choices of <i>values</i> for these parameters
 * (except that it knows how to find the <i>default</i> values).
 */
public final class ParameterSet {
  public static ParameterSet create(
      Class<? extends Benchmark> theClass, Class<? extends Annotation> annotationClass)
          throws InvalidBenchmarkException {
    // deterministic order, not reflection order
    ImmutableMap.Builder<String, Parameter> parametersBuilder =
        ImmutableSortedMap.naturalOrder();

    for (Field field : theClass.getDeclaredFields()) {
      if (field.isAnnotationPresent(annotationClass)) {
        Parameter parameter = Parameter.create(field);
        parametersBuilder.put(field.getName(), parameter);
      }
    }
    return new ParameterSet(parametersBuilder.build());
  }

  final ImmutableMap<String, Parameter> map;

  private ParameterSet(ImmutableMap<String, Parameter> map) {
    this.map = map;
  }

  public Set<String> names() {
    return map.keySet();
  }

  public Parameter get(String name) {
    return map.get(name);
  }

  public ImmutableSetMultimap<String, String> fillInDefaultsFor(
      ImmutableSetMultimap<String, String> explicitValues) throws InvalidBenchmarkException {
    ImmutableSetMultimap.Builder<String, String> combined = ImmutableSetMultimap.builder();

    // For user parameters, this'll actually be the same as fromClass.keySet(), since any extras
    // given at the command line are treated as errors; for VM parameters this is not the case.
    for (String name : Sets.union(map.keySet(), explicitValues.keySet())) {
      Parameter parameter = map.get(name);
      ImmutableCollection<String> values = explicitValues.containsKey(name)
          ? explicitValues.get(name)
          : parameter.defaults();

      combined.putAll(name, values);
      if (values.isEmpty()) {
        throw new InvalidBenchmarkException("ERROR: No default value provided for " + name);
      }
    }
    return combined.orderKeysBy(Ordering.natural()).build();
  }

  public void injectAll(Benchmark benchmark, Map<String, String> actualValues) {
    for (Parameter parameter : map.values()) {
      String value = actualValues.get(parameter.name());
      parameter.inject(benchmark, value);
    }
  }
}
