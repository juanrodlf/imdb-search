package co.empathy.academy.search.services.search;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.DirectCandidateGeneratorBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

public class SuggestQueryBuilder {

    private final String searchText;

    public SuggestQueryBuilder(String searchText) {
        this.searchText = searchText;
    }

    public SearchSourceBuilder addSuggestions() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder<TermSuggestionBuilder> termSuggestionBuilder =
                SuggestBuilders.termSuggestion("primaryTitle.trigram").text(searchText);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("spellcheck", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        return searchSourceBuilder;
    }

    public SearchSourceBuilder addSuggestionsPhrase() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        PhraseSuggestionBuilder phraseSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("primaryTitle.trigram")
                    .addCandidateGenerator(new DirectCandidateGeneratorBuilder("primaryTitle.trigram")
                        .suggestMode("always"))
                        .text(searchText)
                        .maxErrors(4f)
                        .confidence(0f);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("spellcheck", phraseSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        return searchSourceBuilder;
    }

}
