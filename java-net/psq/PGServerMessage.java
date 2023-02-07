package com.andmal.psq;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Future;

sealed interface PGServerMessage permits
        PGServerMessage.AuthenticationRequest, PGServerMessage.BackendKeyData,
        PGServerMessage.ReadyForQuery, PGServerMessage.RowDescription,
        PGServerMessage.DataRow, PGServerMessage.CommandComplete {

    record AuthenticationRequest() implements PGServerMessage {
    }

    record BackendKeyData(int processId, int secretKey) implements PGServerMessage {
    }

    record ReadyForQuery() implements PGServerMessage {
    }

    record RowDescription(List<Field> fields) implements PGServerMessage {

        record Field(
                String name,
                int tableObjectId,
                int columnAttributeNumber,
                int dataTypeObjectId,
                int dataTypeSize,
                int typeModifier,
                int formatCode) {

            int length() {
                // 4 (int tableObjectId) + 2 (short columnAttributeNumber) + 4 (int dataTypeObjectId) + 2 (short dataTypeSize) + 4 (int typeModifier) + 2 (short formatCode)
                // 4 + 2 + 4 + 2 + 4 + 2 = 18
                // Add name length, plus 1 for null terminator '\0'
                return 18 + name.length() + 1;
            }
        }
    }

    record DataRow(List<ByteBuffer> values) implements PGServerMessage {
    }

    record CommandComplete(String commandTag) implements PGServerMessage {
    }

    static ByteBuffer encode(PGServerMessage message) {
        return switch (message) {
            case AuthenticationRequest auth -> {
                var buffer = ByteBuffer.allocate(9);
                buffer.put((byte) 'R'); // 'R' for AuthenticationRequest
                buffer.putInt(8); // Length
                buffer.putInt(0); // Authentication type, 0 for OK
                buffer.flip();
                yield buffer;
            }
            case BackendKeyData keyData -> {
                var buffer = ByteBuffer.allocate(13);
                buffer.put((byte) 'K'); // 'K' for BackendKeyData
                buffer.putInt(12); // Length
                buffer.putInt(keyData.processId()); // Process ID
                buffer.putInt(keyData.secretKey()); // Secret key
                buffer.flip();
                yield buffer;
            }
            case ReadyForQuery ready -> {
                var buffer = ByteBuffer.allocate(6);
                buffer.put((byte) 'Z'); // 'Z' for ReadyForQuery
                buffer.putInt(5); // Length
                buffer.put((byte) 'I'); // Transaction status indicator, 'I' for idle
                buffer.flip();
                yield buffer;
            }
            case RowDescription rowDesc -> {
                var fields = rowDesc.fields();
                var length = 4 + 2 + fields.stream().mapToInt(RowDescription.Field::length).sum();
                var buffer = ByteBuffer.allocate(length + 1)
                        .put((byte) 'T')
                        .putInt(length)
                        .putShort((short) fields.size());
                fields.forEach(field -> buffer
                        .put(field.name().getBytes(StandardCharsets.UTF_8))
                        .put((byte) 0) // null-terminated
                        .putInt(field.tableObjectId())
                        .putShort((short) field.columnAttributeNumber())
                        .putInt(field.dataTypeObjectId())
                        .putShort((short) field.dataTypeSize())
                        .putInt(field.typeModifier())
                        .putShort((short) field.formatCode()));
                buffer.flip();
                yield buffer;
            }
            case DataRow dataRow -> {
                var values = dataRow.values();
                // For each value, we need to add 4 bytes for the length, plus the length of the value
                var length = 4 + 2 + values.stream().map(it -> it.remaining() + 4).reduce(0, Integer::sum);
                var buffer = ByteBuffer.allocate(length + 1) // +1 for msg type
                        .put((byte) 'D')
                        .putInt(length) // +4 for length
                        .putShort((short) values.size()); // +2 for number of columns
                for (var value : values) {
                    buffer.putInt(value.remaining());
                    buffer.put(value);
                }
                buffer.flip();
                yield buffer;
            }
            case CommandComplete cmdComplete -> {
                var commandTag = cmdComplete.commandTag();
                var length = 4 + commandTag.length() + 1;
                yield ByteBuffer.allocate(length + 1) // +1 for msg type
                        .put((byte) 'C')
                        .putInt(length) // +4 for length
                        .put(commandTag.getBytes(StandardCharsets.UTF_8))
                        .put((byte) 0) // null terminator
                        .flip();
            }
        };
    }

    // In "AsynchronousSocketServer"
     static void handleStartupMessage(PGClientMessage.StartupMessage startup, AsynchronousSocketChannel client) {
        System.out.println("[SERVER] Startup Message: " + startup);

        Future<Integer> writeResult;

        // Then, write AuthenticationOk
        PGServerMessage.AuthenticationRequest authRequest = new PGServerMessage.AuthenticationRequest();
        writeResult = client.write(PGServerMessage.encode(authRequest));


        // Then, write BackendKeyData
        PGServerMessage.BackendKeyData backendKeyData = new PGServerMessage.BackendKeyData(1234, 5678);
        writeResult = client.write(PGServerMessage.encode(backendKeyData));

        // Then, write ReadyForQuery
        PGServerMessage.ReadyForQuery readyForQuery = new PGServerMessage.ReadyForQuery();
        writeResult = client.write(PGServerMessage.encode(readyForQuery));

        try {
            writeResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void handleQueryMessage(PGClientMessage.QueryMessage query, AsynchronousSocketChannel client) {
        System.out.println("[SERVER] Query Message: " + query);

        Future<Integer> writeResult;

        // Let's assume it's a query message, and just send a simple response
        // First we send a RowDescription. We'll send two columns, with names "id" and "name"
        PGServerMessage.RowDescription rowDescription = new PGServerMessage.RowDescription(List.of(
                new PGServerMessage.RowDescription.Field("id", 0, 0, 23, 4, -1, 0),
                new PGServerMessage.RowDescription.Field("name", 0, 0, 25, -1, -1, 0)
        ));
        writeResult = client.write(PGServerMessage.encode(rowDescription));


        // Then we send a DataRow for each row. We'll send two rows, with values (1, "one") and (2, "two")
        PGServerMessage.DataRow dataRow1 = new PGServerMessage.DataRow(List.of(
                ByteBuffer.wrap("1".getBytes(StandardCharsets.UTF_8)),
                ByteBuffer.wrap("one".getBytes(StandardCharsets.UTF_8))
        ));
        writeResult = client.write(PGServerMessage.encode(dataRow1));

        PGServerMessage.DataRow dataRow2 = new PGServerMessage.DataRow(List.of(
                ByteBuffer.wrap("2".getBytes(StandardCharsets.UTF_8)),
                ByteBuffer.wrap("two".getBytes(StandardCharsets.UTF_8))
        ));

        writeResult = client.write(PGServerMessage.encode(dataRow2));

        // We send a CommandComplete
        PGServerMessage.CommandComplete commandComplete = new PGServerMessage.CommandComplete("SELECT 2");
        writeResult = client.write(PGServerMessage.encode(commandComplete));

        // Finally, write ReadyForQuery
        PGServerMessage.ReadyForQuery readyForQuery = new PGServerMessage.ReadyForQuery();
        writeResult = client.write(PGServerMessage.encode(readyForQuery));

        try {
            writeResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

