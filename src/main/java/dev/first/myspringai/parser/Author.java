package dev.first.myspringai.parser;

import java.util.List;

public record Author(String author, List<String> books, String synopsis) {
}