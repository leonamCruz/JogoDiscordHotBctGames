package top.leonam.hotbctgamess.listener;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.enums.StoreProduct;
import top.leonam.hotbctgamess.registers.CommandRegistry;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.PlayerRepository;
import top.leonam.hotbctgamess.repository.PrisonRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;
import top.leonam.hotbctgamess.service.BdService;
import top.leonam.hotbctgamess.service.BtcMarketService;
import top.leonam.hotbctgamess.service.CacheService;
import top.leonam.hotbctgamess.service.EnergyService;
import top.leonam.hotbctgamess.service.TaxActionService;
import top.leonam.hotbctgamess.service.TaxService;
import top.leonam.hotbctgamess.util.MiningCalculator;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class JdaListener extends ListenerAdapter {

    private CommandRegistry registry;
    private BdService identityService;
    private EnergyService energyService;
    private EconomyRepository economyRepository;
    private PlayerRepository playerRepository;
    private PrisonRepository prisonRepository;
    private ProductRepository productRepository;
    private BtcMarketService marketService;
    private TaxService taxService;
    private TaxActionService taxActionService;
    private CacheService cacheService;
    private Random random;

    @Transactional
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //if (event.getAuthor().isBot()) return;

        String raw = event.getMessage().getContentRaw();
        String commandName = raw.split("\\s+")[0];

        Command command = registry.get(commandName);

        if (command != null) {
            identityService.saveIdentityIfNotExists(event);

            EmbedBuilder message = command.execute(event);
            var embed = message.build();
            if (shouldAddEnergyButtons(command, embed.getTitle())) {
                event.getMessage()
                        .replyEmbeds(embed)
                        .addComponents(ActionRow.of(
                                Button.of(ButtonStyle.PRIMARY, "energy:auto:" + event.getAuthor().getIdLong(), "Comprar energia ⚡"),
                                Button.of(ButtonStyle.DANGER, "energy:gato:" + event.getAuthor().getIdLong(), "Tentar gato 🐱")
                        ))
                        .queue();
            } else if (shouldAddTaxButtons(command, embed.getTitle())) {
                String rawContent = event.getMessage().getContentRaw();
                ActionRow row = buildTaxActionRow(command.name(), rawContent, event.getAuthor().getIdLong());
                if (row != null) {
                    storeTaxAction(command.name(), rawContent, event.getAuthor().getIdLong());
                    event.getMessage()
                            .replyEmbeds(embed)
                            .addComponents(row)
                            .queue();
                } else {
                    event.getMessage().replyEmbeds(embed).queue();
                }
            } else if (shouldAddPrisonButton(embed.getTitle())) {
                event.getMessage()
                        .replyEmbeds(embed)
                        .addComponents(ActionRow.of(
                                Button.of(ButtonStyle.DANGER, "bail:pay:" + event.getAuthor().getIdLong(), "Pagar fianca 50% 💸")
                        ))
                        .queue();
            } else {
                event.getMessage().replyEmbeds(embed).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        if (!id.startsWith("energy:") && !id.startsWith("tax:") && !id.startsWith("bail:")) {
            return;
        }

        String[] parts = id.split(":");
        if (parts.length < 3) {
            return;
        }

        String action = parts[0];
        long ownerId = 0L;
        if ("tax".equals(action)) {
            if (parts.length < 5) {
                return;
            }
            ownerId = parseLong(parts[3]);
        } else {
            ownerId = parseLong(parts[2]);
        }
        if (ownerId == 0L) {
            return;
        }

        if (event.getUser().getIdLong() != ownerId) {
            event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Esse botao nao e seu 🚫")
                            .setDescription("""
                                    Acesso negado
                                    Apenas o dono do comando pode usar
                                    """)
                            .setAuthor(event.getUser().getEffectiveName())
                            .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                            .setTimestamp(LocalDateTime.now())
                            .setColor(Color.ORANGE)
                            .setFooter("HotBctsGames")
                            .build())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if ("energy".equals(action)) {
            String mode = parts[1];
            if ("auto".equals(mode)) {
                handleAutoEnergy(event);
            } else if ("gato".equals(mode)) {
                handleGato(event);
            }
            return;
        }

        if ("tax".equals(action)) {
            if (parts.length < 5) {
                return;
            }
            String type = parts[1];
            String mode = parts[2];
            boolean evade = "evade".equals(mode);
            TaxActionService.TaxAction actionData = taxActionService.consume(ownerId, type);
            if (actionData == null) {
                respondAndClearButtons(event, buildSimpleError(event, "Acao expirada"));
                return;
            }
            if ("buy".equals(type)) {
                if (!actionData.payload().equals(parts[4])) {
                    respondAndClearButtons(event, buildSimpleError(event, "Acao invalida"));
                    return;
                }
                int storeId = parseInt(parts[4]);
                if (storeId > 0) {
                    handleTaxBuy(event, storeId, evade);
                }
            } else if ("sell".equals(type)) {
                if (!actionData.payload().equals(parts[4])) {
                    respondAndClearButtons(event, buildSimpleError(event, "Acao invalida"));
                    return;
                }
                BigDecimal amount = parseDecimal(parts[4]);
                if (amount != null) {
                    handleTaxSell(event, amount, evade);
                }
            }
            return;
        }

        if ("bail".equals(action)) {
            handleBail(event);
        }
    }

    private void handleAutoEnergy(ButtonInteractionEvent event) {
        Long discordId = event.getUser().getIdLong();
        EnergyService.EnergyStatus status = energyService.getStatus(discordId);
        MiningCalculator.MiningTotals totals = MiningCalculator.calculate(status.products());

        if (totals.btcPerRound().compareTo(BigDecimal.ZERO) <= 0) {
            respondAndClearButtons(event, new EmbedBuilder()
                    .setTitle("Sem maquinas")
                    .setDescription("""
                            Status: Nenhuma maquina encontrada
                            Acao: compre uma GPU ou ASIC na .loja
                            """)
                    .setAuthor(event.getUser().getEffectiveName())
                    .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                    .setTimestamp(LocalDateTime.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames")
                    .build());
            return;
        }

        long energyPerRound = Math.max(1L, totals.energyPerRound());
        long energyAfterDaily = status.currentEnergy();
        if (!status.alreadyPaid()) {
            energyAfterDaily += EnergyService.DAILY_ENERGY_BASE + status.dailyBonus();
        }
        long need = Math.max(0L, energyPerRound - energyAfterDaily);
        int packs = (int) Math.ceil(need / (double) EnergyService.EXTRA_ENERGY_PACK);

        EnergyService.EnergyPurchaseResult result = energyService.purchaseEnergy(discordId, packs, true);
        if (!result.success()) {
            respondAndClearButtons(event, new EmbedBuilder()
                    .setTitle("Dinheiro insuficiente")
                    .setDescription("""
                            Valor necessario: R$%.2f
                            Saldo insuficiente 💸
                            """.formatted(result.totalCost()))
                    .setAuthor(event.getUser().getEffectiveName())
                    .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                    .setTimestamp(LocalDateTime.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames")
                    .build());
            return;
        }

        String infoExtra = result.dailyPaidNow() ? "kWh diario incluido. ✅" : "kWh extra adicionado. ⚡";
        cacheService.evictPlayer(discordId);
        respondAndClearButtons(event, new EmbedBuilder()
                .setTitle("Energia carregada")
                .setDescription("""
                        Valor pago: R$%.2f
                        kWh adicionados: +%d
                        kWh atual: %d ⚡
                        Info: %s
                        """.formatted(result.totalCost(), result.energyAdded(), result.currentEnergy(), infoExtra))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames")
                .build());
    }

    private void handleGato(ButtonInteractionEvent event) {
        Long discordId = event.getUser().getIdLong();
        EnergyService.EnergyStatus status = energyService.getStatus(discordId);
        List<Product> products = new ArrayList<>(status.products());

        List<Product> asicProducts = new ArrayList<>();
        for (Product product : products) {
            Integer storeId = product.getStoreProductId();
            if (storeId == null) {
                continue;
            }
            StoreProduct storeProduct = StoreProduct.fromId(storeId);
            if (storeProduct != null && storeProduct.isAsic()) {
                asicProducts.add(product);
            }
        }

        boolean sucesso = random.nextInt(100) < 50;
        if (sucesso) {
            Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
            long energiaGato = EnergyService.EXTRA_ENERGY_PACK * 2;
            economy.setEnergy((economy.getEnergy() == null ? 0L : economy.getEnergy()) + energiaGato);
            economyRepository.save(economy);
            cacheService.evictPlayer(discordId);

            respondAndClearButtons(event, new EmbedBuilder()
                    .setTitle("Gato ligado com sucesso 🐱")
                    .setDescription("""
                            kWh clandestino: +%d ⚡
                            Status: Sem ser pego ✅
                            """.formatted(energiaGato))
                    .setAuthor(event.getUser().getEffectiveName())
                    .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                    .setTimestamp(LocalDateTime.now())
                    .setColor(Color.GREEN)
                    .setFooter("HotBctsGames")
                    .build());
            return;
        }

        int toRemove = (int) Math.ceil(asicProducts.size() / 2.0);
        if (toRemove > 0) {
            productRepository.deleteAll(asicProducts.subList(0, toRemove));
        }
        cacheService.evictPlayer(discordId);

        respondAndClearButtons(event, new EmbedBuilder()
                .setTitle("Gato descoberto 🚨")
                .setDescription("""
                        Status: Companhia descobriu ⚠️
                        ASICs perdidos: %d
                        """.formatted(toRemove))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames")
                .build());
    }

    private void handleTaxBuy(ButtonInteractionEvent event, int storeId, boolean evade) {
        StoreProduct storeProduct = StoreProduct.fromId(storeId);
        if (storeProduct == null) {
            respondAndClearButtons(event, buildSimpleError(event, "Produto nao encontrado"));
            return;
        }

        Long discordId = event.getUser().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        BigDecimal price = storeProduct.getPrice();
        BigDecimal tax = taxService.calculateTax(price);
        BigDecimal fine = taxService.calculateFine(price);

        BigDecimal total = price.add(tax);
        BigDecimal totalIfCaught = total.add(fine);

        if (!evade) {
            if (safeMoney(economy.getMoney()).compareTo(total) < 0) {
                respondAndClearButtons(event, buildMoneyError(event, total));
                return;
            }
            processPurchase(discordId, storeProduct, total);
            cacheService.evictPlayer(discordId);
            respondAndClearButtons(event, buildTaxResult(
                    event,
                    "Imposto pago ✅",
                    storeProduct.getName(),
                    total,
                    price,
                    tax,
                    BigDecimal.ZERO
            ));
            return;
        }

        boolean caught = taxService.isCaught();
        if (caught) {
            if (safeMoney(economy.getMoney()).compareTo(totalIfCaught) < 0) {
                respondAndClearButtons(event, buildMoneyError(event, totalIfCaught));
                return;
            }
            processPurchase(discordId, storeProduct, totalIfCaught);
            cacheService.evictPlayer(discordId);
            respondAndClearButtons(event, buildTaxResult(
                    event,
                    "Sonegacao falhou 🚨",
                    storeProduct.getName(),
                    totalIfCaught,
                    price,
                    tax,
                    fine
            ));
            return;
        }

        if (safeMoney(economy.getMoney()).compareTo(price) < 0) {
            respondAndClearButtons(event, buildMoneyError(event, price));
            return;
        }
        processPurchase(discordId, storeProduct, price);
        cacheService.evictPlayer(discordId);
        respondAndClearButtons(event, buildTaxResult(
                event,
                "Sonegacao ok 🐱",
                storeProduct.getName(),
                price,
                price,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        ));
    }

    private void handleTaxSell(ButtonInteractionEvent event, BigDecimal amount, boolean evade) {
        Long discordId = event.getUser().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        BigDecimal btc = economy.getBtc() == null ? BigDecimal.ZERO : economy.getBtc();
        if (btc.compareTo(amount) < 0) {
            respondAndClearButtons(event, buildSimpleError(event, "BTC insuficiente"));
            return;
        }

        BigDecimal price = marketService.getCurrentPrice();
        BigDecimal gross = amount.multiply(price);
        BigDecimal tax = taxService.calculateTax(gross);
        BigDecimal fine = taxService.calculateFine(gross);

        BigDecimal net = gross;
        String status = "Imposto pago ✅";
        if (evade) {
            boolean caught = taxService.isCaught();
            if (caught) {
                net = gross.subtract(tax).subtract(fine);
                status = "Sonegacao falhou 🚨";
            } else {
                status = "Sonegacao ok 🐱";
            }
        } else {
            net = gross.subtract(tax);
        }

        if (net.compareTo(BigDecimal.ZERO) < 0) {
            net = BigDecimal.ZERO;
        }

        economy.setBtc(btc.subtract(amount));
        economy.setMoney(safeMoney(economy.getMoney()).add(net));
        economyRepository.save(economy);
        marketService.applySell(amount);
        cacheService.evictPlayer(discordId);

        respondAndClearButtons(event, new EmbedBuilder()
                .setTitle("Venda de BTC")
                .setDescription("""
                        Status: %s
                        BTC vendido: %.5f
                        Preco BTC: R$%.2f
                        Valor bruto: R$%.2f
                        Imposto: R$%.2f
                        Multa: R$%.2f
                        Valor recebido: R$%.2f
                        """.formatted(status, amount, price, gross, tax, fine, net))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames")
                .build());
    }

    private void handleBail(ButtonInteractionEvent event) {
        Long discordId = event.getUser().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        var prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);
        if (prison == null || prison.getLastPrison() == null) {
            respondAndClearButtons(event, new EmbedBuilder()
                    .setTitle("Sem prisao ativa")
                    .setDescription("""
                            Status: Voce nao esta preso
                            """)
                    .setAuthor(event.getUser().getEffectiveName())
                    .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                    .setTimestamp(LocalDateTime.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames")
                    .build());
            return;
        }

        BigDecimal money = safeMoney(economy.getMoney());
        BigDecimal fee = money.multiply(new BigDecimal("0.50"));
        economy.setMoney(money.subtract(fee));
        economyRepository.save(economy);

        prison.setLastPrison(null);
        prison.setActive(false);
        prisonRepository.save(prison);
        cacheService.evictPlayer(discordId);

        respondAndClearButtons(event, new EmbedBuilder()
                .setTitle("Fianca paga ⚖️")
                .setDescription("""
                        Valor pago: R$%.2f
                        Status: Liberado ✅
                        """.formatted(fee))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames")
                .build());
    }

    private void processPurchase(Long discordId, StoreProduct storeProduct, BigDecimal totalCost) {
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        Player player = playerRepository.findByIdentity_DiscordId(discordId).orElseThrow();

        Product product = new Product();
        product.setName(storeProduct.getName());
        product.setDescription(storeProduct.getDescription());
        product.setPrice(storeProduct.getPrice());
        product.setStoreProductId(storeProduct.getId());
        product.setPlayer(player);
        productRepository.save(product);

        economy.setMoney(safeMoney(economy.getMoney()).subtract(totalCost));
        economyRepository.save(economy);
    }

    private MessageEmbed buildMoneyError(ButtonInteractionEvent event, BigDecimal needed) {
        return new EmbedBuilder()
                .setTitle("Dinheiro insuficiente")
                .setDescription("""
                        Valor necessario: R$%.2f
                        Saldo insuficiente 💸
                        """.formatted(needed))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames")
                .build();
    }

    private MessageEmbed buildSimpleError(ButtonInteractionEvent event, String message) {
        return new EmbedBuilder()
                .setTitle("Erro")
                .setDescription(message)
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames")
                .build();
    }

    private MessageEmbed buildTaxResult(
            ButtonInteractionEvent event,
            String status,
            String productName,
            BigDecimal paid,
            BigDecimal price,
            BigDecimal tax,
            BigDecimal fine
    ) {
        return new EmbedBuilder()
                .setTitle("Compra concluida")
                .setDescription("""
                        Status: %s
                        Produto: %s
                        Preco: R$%.2f
                        Imposto: R$%.2f
                        Multa: R$%.2f
                        Total pago: R$%.2f
                        """.formatted(status, productName, price, tax, fine, paid))
                .setAuthor(event.getUser().getEffectiveName())
                .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames")
                .build();
    }

    private boolean shouldAddEnergyButtons(Command command, String title) {
        if (!".minerar".equals(command.name())) {
            return false;
        }
        if (title == null) {
            return false;
        }
        return Set.of("Sem energia suficiente", "Energia pendente").contains(title);
    }

    private boolean shouldAddTaxButtons(Command command, String title) {
        return "Imposto necessario".equals(title)
                && (".comprar".equals(command.name()) || ".venderbtc".equals(command.name()));
    }

    private boolean shouldAddPrisonButton(String title) {
        return "Você está preso 🚓".equals(title);
    }

    private ActionRow buildTaxActionRow(String commandName, String raw, long authorId) {
        if (".comprar".equals(commandName)) {
            Integer storeId = parseStoreId(raw);
            if (storeId == null) {
                return null;
            }
            return ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "tax:buy:pay:" + authorId + ":" + storeId, "Pagar imposto 💸"),
                    Button.of(ButtonStyle.DANGER, "tax:buy:evade:" + authorId + ":" + storeId, "Sonegar 🐱")
            );
        }
        if (".venderbtc".equals(commandName)) {
            String amount = parseAmount(raw);
            if (amount == null) {
                return null;
            }
            return ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "tax:sell:pay:" + authorId + ":" + amount, "Pagar imposto 💸"),
                    Button.of(ButtonStyle.DANGER, "tax:sell:evade:" + authorId + ":" + amount, "Sonegar 🐱")
            );
        }
        return null;
    }

    private void storeTaxAction(String commandName, String raw, long authorId) {
        if (".comprar".equals(commandName)) {
            Integer storeId = parseStoreId(raw);
            if (storeId != null) {
                taxActionService.store(authorId, new TaxActionService.TaxAction("buy", String.valueOf(storeId)));
            }
        }
        if (".venderbtc".equals(commandName)) {
            String amount = parseAmount(raw);
            if (amount != null) {
                taxActionService.store(authorId, new TaxActionService.TaxAction("sell", amount));
            }
        }
    }

    private Integer parseStoreId(String raw) {
        String[] parts = raw.trim().split("\\s+");
        if (parts.length < 2) {
            return null;
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String parseAmount(String raw) {
        String[] parts = raw.trim().split("\\s+");
        if (parts.length < 2) {
            return null;
        }
        return parts[1];
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void respondAndClearButtons(ButtonInteractionEvent event, MessageEmbed embed) {
        event.deferEdit().queue();
        event.getMessage().editMessageComponents().queue();
        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
