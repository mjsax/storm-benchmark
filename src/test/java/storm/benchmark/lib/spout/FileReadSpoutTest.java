/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package storm.benchmark.lib.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import storm.benchmark.tools.FileReader;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class FileReadSpoutTest {
  private static final int messageSize = 100;
  private static final Map ANY_CONF = new HashMap();
  private static final String NEXT_LINE = "next line";
  private OutputFieldsDeclarer declarer;
  private TopologyContext context;
  private SpoutOutputCollector collector;
  private FileReader reader;

  @BeforeMethod
  public void setUp() {
    declarer = mock(OutputFieldsDeclarer.class);
    collector = mock(SpoutOutputCollector.class);
    context = mock(TopologyContext.class);
    reader = mock(FileReader.class);
    when(reader.nextLine()).thenReturn(NEXT_LINE);
  }

  @Test
  public void shouldDeclareOutputFields() {
    FileReadSpout spout = new FileReadSpout(false, reader);

    spout.declareOutputFields(declarer);

    verify(declarer, times(1)).declare(any(Fields.class));
  }

  @Test
  public void shouldEmitValueAndIdWhenAckEnabled() {
    FileReadSpout spout = new FileReadSpout(true, reader);

    spout.open(ANY_CONF, context, collector);
    verify(reader, times(1)).open();

    spout.nextTuple();
    verify(collector, times(1)).emit(any(Values.class), anyInt());
  }

  @Test
  public void shouldEmitValueOnlyWhenAckDisabled() {
    FileReadSpout spout = new FileReadSpout(false, reader);

    spout.open(ANY_CONF, context, collector);
    verify(reader, times(1)).open();

    spout.nextTuple();
    verify(collector, times(1)).emit(any(Values.class));
  }
}
