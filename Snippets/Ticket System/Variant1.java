public class Ticket extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase("CreateTicket")) {
            if(event.isFromType(ChannelType.PRIVATE) || event.isFromType(ChannelType.GROUP)) return;

            event.getMessage().delete().queue();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(":incoming_envelope: Ticket - Support :incoming_envelope:");
            eb.setDescription("Click on the button corresponding to the type of ticket you wish to open");
            eb.addField(":gear: Categories :gear:", ""
                    + "⭕ General ticket\n"
                    + "⭕ Report Ticket\n"
                    + "⭕ Admin Ticket\n We are looking forward to it!", false);
            eb.addField(":gear: How to create a ticket? :gear:", ""
                    + "⭕ Click on the Selection menu and choose your category. \n"
                    + "⭕ Share your concern with us \n"
                    + "⭕ Wait for a supporter to contact you.", false);
            eb.setFooter("Made by daedalusdontknow");

            eb.setColor(Color.RED);
            eb.setThumbnail(event.getGuild().getIconUrl());

            event.getChannel().sendMessageEmbeds(eb.build())
                    .addActionRow(SelectMenu.create("Support")
                            .setPlaceholder("Choose your ticket category")
                            .setRequiredRange(1, 1)

                            .addOptions(SelectOption.of("General ticket", "general")
                                    .withDescription("create a ticket in the \"General\" category ")
                                    .withEmoji(Emoji.fromUnicode("✉")))

                            .addOptions(SelectOption.of("Report Ticket", "report")
                                    .withDescription("create a ticket in the \"Report\" category ")
                                    .withEmoji(Emoji.fromUnicode("🔨")))

                            .addOptions(SelectOption.of("Admin Ticket", "admin")
                                    .withDescription("create a ticket in the \"Admin\" category ")
                                    .withEmoji(Emoji.fromUnicode("✉")))
                            .build())
                    .queue();
        }
    }
    
    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        switch (event.getValues().get(0)){
            case "general" : createTicketChannel(event, event.getGuild().getNewsChannelsByName("General-category", true).get(0).getIdLong(), "general", "General Support"); break;
            case "report" : createTicketChannel(event, event.getGuild().getNewsChannelsByName("report-category", true).get(0).getIdLong(), "report", "Report Ticket"); break;
            case "admin" : createTicketChannel(event, event.getGuild().getNewsChannelsByName("admin-category", true).get(0).getIdLong(), "admin", "Admin Ticket"); break;
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getButton().getId().startsWith("taketicket")){
            String[] id = event.getButton().getId().split("_");
            event.getChannel().sendMessage(":gear: <@" + id[1] + "> you will now be supported by " + event.getMember().getUser().getAsMention() + " :gear:").queue();
            event.reply(":people_hugging: Support adopted :people_hugging:").setEphemeral(true).queue();
        }
        if(event.getButton().getId().startsWith("closeticket")){
            String[] id = event.getButton().getId().split("_");
            event.getChannel().sendMessage(":wastebasket: This ticket will be deleted in 20 seconds <@" + id[1] + "> :wastebasket:").queue();
            event.reply(":people_hugging: Channel will be deleted :people_hugging:").setEphemeral(true).queue();
            event.getChannel().delete().queueAfter(20, TimeUnit.SECONDS);
        }
    }

    public static void createTicketChannel(SelectMenuInteractionEvent event, Long category, String ticketID, String reason) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(event.getUser().getName() + "`s Ticket");
        eb.setDescription("A team member will take care of you soon, be patient!");
        eb.addField(":gear: Informations :gear:",
                "**Creator** " + " ⭕ *" + event.getMember().getAsMention() + "*" +
                        "\n**TicketTopic** " + " ⭕ *" + reason + "*" +
                        "\n**UserID** " + " ⭕ *" + event.getMember().getId() + "*", false);
        eb.setThumbnail(event.getGuild().getIconUrl());
        eb.setColor(Color.BLUE);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("taketicket_" + event.getUser().getId(), "Take Support"));
        buttons.add(Button.danger("closeticket_" + event.getUser().getId(), "Close Support"));

        event.getGuild().createTextChannel("ticket-" + ticketID + "-" + event.getUser().getName(), event.getGuild().getCategoryById(category))
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(event.getGuild().getRoleById(String.valueOf(event.getGuild().getRolesByName("👤 Team", false).get(0))), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .complete()
                .sendMessageEmbeds(eb.build()).setActionRow(buttons).complete()
                .getChannel().sendMessage(event.getGuild().getRolesByName("👤 Team", false).get(0).getAsMention() + "").queue();
    }
}
