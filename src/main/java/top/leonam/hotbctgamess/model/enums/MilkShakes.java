package top.leonam.hotbctgamess.model.enums;

public enum MilkShakes {

    BAUNILHA("Baunilha", "🍦"),
    CHOCOLATE("Chocolate", "🍫"),
    MORANGO("Morango", "🍓"),
    OREO("Oreo", "🍪"),
    BANANA("Banana", "🍌"),
    PACOCA("Paçoca", "🥜");

    private final String nome;
    private final String emoji;

    MilkShakes(String nome, String emoji) {
        this.nome = nome;
        this.emoji = emoji;
    }

    public String getNome() {
        return nome;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getTextoFormatado() {
        return "Sabor de Milk Shake de %s. ˗ˏˋ %s ˎˊ˗".formatted(nome, emoji);
    }
}


