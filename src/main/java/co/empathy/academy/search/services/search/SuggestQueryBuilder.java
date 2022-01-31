package co.empathy.academy.search.services.search;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

public class SuggestQueryBuilder {

    private final String searchText;

    public SuggestQueryBuilder(String searchText) {
        this.searchText = searchText;
    }

    public SearchSourceBuilder addSuggestions() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder<TermSuggestionBuilder> termSuggestionBuilder =
                SuggestBuilders.termSuggestion("primaryTitle").text(searchText);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("spellcheck", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        return searchSourceBuilder;
    }

}
