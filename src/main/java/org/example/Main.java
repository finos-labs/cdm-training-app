package org.example;


import cdm.base.math.NonNegativeQuantitySchedule;
import cdm.base.math.UnitType;
import cdm.base.math.metafields.FieldWithMetaNonNegativeQuantitySchedule;
import cdm.base.staticdata.asset.common.*;
import cdm.base.staticdata.asset.common.metafields.ReferenceWithMetaProductIdentifier;
import cdm.base.staticdata.party.*;
import cdm.event.common.*;
import cdm.event.common.functions.Create_BusinessEvent;
import cdm.event.common.functions.Create_TerminationInstruction;
import cdm.event.workflow.EventTimestamp;
import cdm.event.workflow.MessageInformation;
import cdm.event.workflow.Workflow;
import cdm.event.workflow.WorkflowStep;
import cdm.event.workflow.functions.Create_WorkflowStep;
import cdm.observable.asset.Price;
import cdm.observable.asset.PriceExpressionEnum;
import cdm.observable.asset.PriceTypeEnum;
import cdm.observable.asset.metafields.FieldWithMetaPriceSchedule;
import cdm.product.common.settlement.PriceQuantity;
import cdm.product.template.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import com.rosetta.model.lib.GlobalKey;
import com.rosetta.model.lib.RosettaModelObject;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.records.Date;
import com.rosetta.model.metafields.FieldWithMetaDate;
import cdm.base.staticdata.identifier.*;
import com.rosetta.model.metafields.FieldWithMetaString;
import com.rosetta.model.metafields.MetaFields;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import cdm.base.staticdata.identifier.AssignedIdentifier;
import cdm.base.staticdata.party.Party;

import com.regnosys.rosetta.common.hashing.GlobalKeyProcessStep;
import com.regnosys.rosetta.common.hashing.NonNullHashCollector;
import com.rosetta.model.lib.process.PostProcessStep;
import org.finos.cdm.CdmRuntimeModule;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private final PostProcessStep keyProcessor;

    public Main(PostProcessStep keyProcessor) {
        this.keyProcessor = keyProcessor;
    }

    private <T extends RosettaModelObject> T addGlobalKey(Class<T> type, T modelObject) {
        RosettaModelObjectBuilder builder = modelObject.toBuilder();
        keyProcessor.runProcessStep(type, builder);
        return type.cast(builder.build());
    }
    private String getGlobalReference(GlobalKey globalKey) {
        return globalKey.getMeta().getGlobalKey();
    }
    public static void main(String[] args) throws IOException {
        System.out.println("CDM Application Demo");
        workflowPlan();

    }

    public static void workflowPlan() throws IOException {

        String businessEventJson = executeTrade();

        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        BusinessEvent businessEventObj = new BusinessEvent.BusinessEventBuilderImpl();
        businessEventObj  = rosettaObjectMapper.readValue(businessEventJson, businessEventObj.getClass());

        Workflow newTrade = createNewTradeWorkflow(businessEventObj);
        String workflowJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newTrade);
        DateTimeFormatter eventDateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String eventDateTime = localDateTime.format(eventDateTimeFormat);
        ExampleFileWriter fileWriter = new ExampleFileWriter();
        fileWriter.writeEventToFile("NewTradeWorkflow", eventDateTime, workflowJson);

        TradeState tradeState = businessEventObj.getAfter().get(0);

        String tradeStateJson= RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tradeState);

        String cancelEventJson = terminateTrade(tradeStateJson);

        businessEventObj  = rosettaObjectMapper.readValue(cancelEventJson, businessEventObj.getClass());

        Workflow cancelTrade = cancelTradeWorkflow(businessEventObj);
        workflowJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newTrade);
        fileWriter.writeEventToFile("CancelWorkflow", eventDateTime, workflowJson);

    }


    public static Workflow createNewTradeWorkflow(BusinessEvent tradeEvent){

            //Repo Execution Step
            Create_WorkflowStep wfs = new Create_WorkflowStep.Create_WorkflowStepDefault();
            Injector injector = Guice.createInjector(new CdmRuntimeModule());
            injector.injectMembers(wfs);

            DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime localDateTime = LocalDateTime.now();
            String eventDateTime = localDateTime.format(eventDateFormat);
            EventTimestamp eventTimeStamp = new EventTimestamp.EventTimestampBuilderImpl();
            eventTimeStamp.getDateTime();
            List<EventTimestamp> eventTimestampList = List.of(eventTimeStamp);

            Identifier eventIdentifier = new Identifier.IdentifierBuilderImpl();
            eventIdentifier.hashCode();
            List<Identifier> eventIdList = List.of(eventIdentifier);

            Party party1 = Party.builder()
                    .setNameValue("BANK1")
                    .build();

            Party party2 = Party.builder()
                    .setNameValue("BANK2")
                    .build();

            List<Party> parties = List.of(party1, party2);

            MessageInformation messageInformation = null;
            List<Account> accountList = null;
            WorkflowStep previousWorkflowStep = null;

            List<WorkflowStep> repoWorkflowList = List.of(
                    wfs.evaluate(
                            messageInformation,
                            eventTimestampList,
                            eventIdList,
                            parties,
                            accountList,
                            previousWorkflowStep,
                            ActionEnum.NEW,
                            tradeEvent
                    )
            );

            Workflow rwf = new Workflow.WorkflowBuilderImpl()
                .addSteps(repoWorkflowList)
                .build();

            return rwf;

        }

    public static Workflow cancelTradeWorkflow(BusinessEvent tradeEvent){

        //Repo Execution Step
        Create_WorkflowStep wfs = new Create_WorkflowStep.Create_WorkflowStepDefault();
        Injector injector = Guice.createInjector(new CdmRuntimeModule());
        injector.injectMembers(wfs);

        DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String eventDateTime = localDateTime.format(eventDateFormat);
        EventTimestamp eventTimeStamp = new EventTimestamp.EventTimestampBuilderImpl();
        eventTimeStamp.getDateTime();
        List<EventTimestamp> eventTimestampList = List.of(eventTimeStamp);

        Identifier eventIdentifier = new Identifier.IdentifierBuilderImpl();
        eventIdentifier.hashCode();
        List<Identifier> eventIdList = List.of(eventIdentifier);

        Party party1 = Party.builder()
                .setNameValue("BANK1")
                .build();

        Party party2 = Party.builder()
                .setNameValue("BANK2")
                .build();

        List<Party> parties = List.of(party1, party2);

        MessageInformation messageInformation = null;
        List<Account> accountList = null;
        WorkflowStep previousWorkflowStep = null;

        List<WorkflowStep> repoWorkflowList = List.of(
                wfs.evaluate(
                        messageInformation,
                        eventTimestampList,
                        eventIdList,
                        parties,
                        accountList,
                        previousWorkflowStep,
                        ActionEnum.NEW,
                        tradeEvent
                )
        );

        Workflow rwf = new Workflow.WorkflowBuilderImpl()
                .addSteps(repoWorkflowList)
                .build();

        return rwf;

    }

    public static String executeTrade() throws IOException {
        Party party1 = Party.builder()
                .setNameValue("BANK1")
                .build();

        Party party2 = Party.builder()
                .setNameValue("BANK2")
                .build();

        List<Party> parties = List.of(party1, party2);

        PartyRole party1Role = PartyRole.builder()
                .setRole(PartyRoleEnum.BUYER)
                .build();

        PartyRole party2Role = PartyRole.builder()
                .setRole(PartyRoleEnum.SELLER)
                .build();

        List<PartyRole> partyRoles = List.of(party1Role, party2Role);

        FieldWithMetaDate tradeDate = FieldWithMetaDate.builder().setValue(Date.of(2024, 3, 28)).build();

        TradeIdentifier tradeId = TradeIdentifier.builder().addAssignedIdentifier(
                        AssignedIdentifier.builder().
                                setIdentifier(
                                        FieldWithMetaString.builder().setValue("5493000SCC07UI6DB380")
                                                .setMeta(MetaFields.builder()
                                                        .setScheme("UTI"))))
                .setIdentifierType(TradeIdentifierTypeEnum.valueOf("UNIQUE_TRANSACTION_IDENTIFIER"))
                .build();

        ExecutionDetails executionDetails = ExecutionDetails.builder()

                .setExecutionType(ExecutionTypeEnum.valueOf("OFF_FACILITY"))
                .setExecutionVenue(LegalEntity.builder()
                        .setEntityId(List.of(FieldWithMetaString.builder()
                                .setValue("OTC")
                                .setMeta(MetaFields.builder()
                                        .setScheme("MIC")).build()))
                        .setName(FieldWithMetaString.builder()
                                .setValue("BOND_ATS")
                                .setMeta(MetaFields.builder()
                                        .setScheme("MIC")).build()))
                .build();

        Product product = Product.builder()
                .setSecurity(Security.builder()
                        .setProductIdentifier(List.of(ReferenceWithMetaProductIdentifier.builder()
                                .setValue(ProductIdentifier.builder()
                                        .setSource(ProductIdTypeEnum.ISIN)
                                        .setIdentifier(FieldWithMetaString.builder()
                                                .setValue("GB00BD0PCK97")))))
                        .build());

        PriceQuantity priceQuantity = PriceQuantity.builder()

                .addPrice(FieldWithMetaPriceSchedule.builder()
                        .setValue(Price.builder()
                                .setValue(BigDecimal.valueOf(100.25))
                                .setUnit(UnitType.builder()
                                        .setCurrencyValue("GBP"))
                                .setPerUnitOf(UnitType.builder()
                                        .setCurrencyValue("GBP"))
                                .setPriceType(PriceTypeEnum.ASSET_PRICE)
                                .setPriceExpression(PriceExpressionEnum.PERCENTAGE_OF_NOTIONAL)))

                .addQuantity(FieldWithMetaNonNegativeQuantitySchedule.builder()
                        .setValue(NonNegativeQuantitySchedule.builder()
                                .setValue(BigDecimal.valueOf(1000))
                                .setUnit(UnitType.builder()
                                        .setCurrencyValue("GBP")))
                        .build())

                .build();

        List<Counterparty> counterparties = List.of(Counterparty.builder()
                .setRole(CounterpartyRoleEnum.PARTY_1)
                .setPartyReferenceValue(party1)
                .setRole(CounterpartyRoleEnum.PARTY_2)
                .setPartyReferenceValue(party2)
                .build());


        ExecutionInstruction executionInstruction = ExecutionInstruction.builder()
                .setProduct(product)
                .addPriceQuantity(priceQuantity)
                .addCounterparty(counterparties)
                .addParties(parties)
                .addPartyRoles(partyRoles)
                .setExecutionDetails(executionDetails)
                .setTradeDate(tradeDate)
                .addTradeIdentifier(tradeId)
                .build();


        Instruction instruction = Instruction.builder()
                .setPrimitiveInstruction(PrimitiveInstruction.builder()
                        .setExecution(executionInstruction))
                .setBeforeValue(null)
                .build();

        List<Instruction> instructionList = List.of(instruction);

        Injector injector = Guice.createInjector(new CdmRuntimeModule());

        Create_BusinessEvent execEvent = new Create_BusinessEvent.Create_BusinessEventDefault();
        injector.injectMembers(execEvent);

        DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDateTime.now();
        String eventDateStr = localDateTime.format(eventDateFormat);

        Date effectiveDate = Date.of(2024, 3, 28);
        Date eventDate = Date.of(2024, 3, 28);
        BusinessEvent businessEvent = execEvent.evaluate(instructionList, null, eventDate, effectiveDate);

        String businesseventJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(businessEvent);

        DateTimeFormatter eventDateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String eventDateTime = localDateTime.format(eventDateTimeFormat);

        ExampleFileWriter fileWriter = new ExampleFileWriter();

        fileWriter.writeEventToFile("Execution", eventDateTime, businesseventJson);

        return businesseventJson;

    }


    public static String terminateTrade(String tradeStateJson) throws IOException {

        Injector injector = Guice.createInjector(new CdmRuntimeModule());

        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        TradeState tradeStateObj = new TradeState.TradeStateBuilderImpl();
        TradeState tradeState  = rosettaObjectMapper.readValue(tradeStateJson, tradeStateObj.getClass());


        Create_TerminationInstruction createTerminationInst = new Create_TerminationInstruction.Create_TerminationInstructionDefault();
        injector.injectMembers(createTerminationInst);
        PrimitiveInstruction terminationInstruction = createTerminationInst.evaluate(tradeState);

        Instruction instruction = Instruction.builder()
                .setPrimitiveInstruction(terminationInstruction)
                .setBeforeValue(tradeState)
                .build();

        List<Instruction> instructionList = List.of(instruction);


        Create_BusinessEvent terminationEvent = new Create_BusinessEvent.Create_BusinessEventDefault();
        injector.injectMembers(terminationEvent);

        DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDateTime.now();
        String eventDateStr = localDateTime.format(eventDateFormat);

        Date effectiveDate = Date.of(2024, 3, 28);
        Date eventDate = Date.of(2024, 3, 28);
        BusinessEvent businessEvent = terminationEvent.evaluate(instructionList, null, eventDate, effectiveDate);

        String businesseventJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(businessEvent);

        DateTimeFormatter eventDateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String eventDateTime = localDateTime.format(eventDateFormat);

        ExampleFileWriter fileWriter = new ExampleFileWriter();

        fileWriter.writeEventToFile("Termination", eventDateTime, businesseventJson);


        return businesseventJson;
    }

    public static String getTradeState(String businessEventJson) throws JsonProcessingException {

        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getNewRosettaObjectMapper();
        BusinessEvent businessEventObj = new BusinessEvent.BusinessEventBuilderImpl();
        BusinessEvent businessEvent  = rosettaObjectMapper.readValue(businessEventJson, businessEventObj.getClass());


        TradeState tradeStateObj = new TradeState.TradeStateBuilderImpl();
        TradeState tradeState  = businessEvent.getAfter().get(0);

        String tradeStateJson = RosettaObjectMapper.getNewRosettaObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tradeState);

        return tradeStateJson;

    }


}