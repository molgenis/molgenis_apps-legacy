SELECT 'MolgenisRole' AS entity, count(*) AS count FROM molgenisRole
 UNION 
SELECT 'MolgenisGroup' AS entity, count(*) AS count FROM molgenisGroup
 UNION 
SELECT 'MolgenisRoleGroupLink' AS entity, count(*) AS count FROM molgenisRoleGroupLink
 UNION 
SELECT 'Person' AS entity, count(*) AS count FROM person
 UNION 
SELECT 'Institute' AS entity, count(*) AS count FROM institute
 UNION 
SELECT 'MolgenisUser' AS entity, count(*) AS count FROM molgenisUser
 UNION 
SELECT 'MolgenisPermission' AS entity, count(*) AS count FROM molgenisPermission
 UNION 
SELECT 'OntologyTerm' AS entity, count(*) AS count FROM ontologyTerm
 UNION 
SELECT 'Ontology' AS entity, count(*) AS count FROM ontology
 UNION 
SELECT 'MolgenisFile' AS entity, count(*) AS count FROM molgenisFile
 UNION 
SELECT 'RuntimeProperty' AS entity, count(*) AS count FROM runtimeProperty
 UNION 
SELECT 'Publication' AS entity, count(*) AS count FROM publication
 UNION 
SELECT 'UseCase' AS entity, count(*) AS count FROM useCase
 UNION 
SELECT 'MolgenisEntity' AS entity, count(*) AS count FROM molgenisEntity
 UNION 
SELECT 'ObservedInference' AS entity, count(*) AS count FROM observedInference
 UNION 
SELECT 'DataFile' AS entity, count(*) AS count FROM dataFile
 UNION 
SELECT 'Data' AS entity, count(*) AS count FROM data
 UNION 
SELECT 'BinaryDataMatrix' AS entity, count(*) AS count FROM binaryDataMatrix
 UNION 
SELECT 'CSVDataMatrix' AS entity, count(*) AS count FROM cSVDataMatrix
 UNION 
SELECT 'DecimalDataElement' AS entity, count(*) AS count FROM decimalDataElement
 UNION 
SELECT 'TextDataElement' AS entity, count(*) AS count FROM textDataElement
 UNION 
SELECT 'OriginalFile' AS entity, count(*) AS count FROM originalFile
 UNION 
SELECT 'Investigation' AS entity, count(*) AS count FROM investigation
 UNION 
SELECT 'Species' AS entity, count(*) AS count FROM species
 UNION 
SELECT 'AlternateId' AS entity, count(*) AS count FROM alternateId
 UNION 
SELECT 'ObservationElement' AS entity, count(*) AS count FROM observationElement
 UNION 
SELECT 'ObservationTarget' AS entity, count(*) AS count FROM observationTarget
 UNION 
SELECT 'ObservableFeature' AS entity, count(*) AS count FROM observableFeature
 UNION 
SELECT 'Measurement' AS entity, count(*) AS count FROM measurement
 UNION 
SELECT 'Category' AS entity, count(*) AS count FROM category
 UNION 
SELECT 'Individual' AS entity, count(*) AS count FROM individual
 UNION 
SELECT 'Location' AS entity, count(*) AS count FROM location
 UNION 
SELECT 'Panel' AS entity, count(*) AS count FROM panel
 UNION 
SELECT 'ObservedValue' AS entity, count(*) AS count FROM observedValue
 UNION 
SELECT 'Protocol' AS entity, count(*) AS count FROM protocol
 UNION 
SELECT 'ProtocolApplication' AS entity, count(*) AS count FROM protocolApplication
 UNION 
SELECT 'ProtocolDocument' AS entity, count(*) AS count FROM protocolDocument
 UNION 
SELECT 'Workflow' AS entity, count(*) AS count FROM workflow
 UNION 
SELECT 'WorkflowElement' AS entity, count(*) AS count FROM workflowElement
 UNION 
SELECT 'WorkflowElementParameter' AS entity, count(*) AS count FROM workflowElementParameter
 UNION 
SELECT 'Chromosome' AS entity, count(*) AS count FROM chromosome
 UNION 
SELECT 'NMRBin' AS entity, count(*) AS count FROM nMRBin
 UNION 
SELECT 'Clone' AS entity, count(*) AS count FROM clone
 UNION 
SELECT 'DerivedTrait' AS entity, count(*) AS count FROM derivedTrait
 UNION 
SELECT 'EnvironmentalFactor' AS entity, count(*) AS count FROM environmentalFactor
 UNION 
SELECT 'Gene' AS entity, count(*) AS count FROM gene
 UNION 
SELECT 'Transcript' AS entity, count(*) AS count FROM transcript
 UNION 
SELECT 'Protein' AS entity, count(*) AS count FROM protein
 UNION 
SELECT 'Metabolite' AS entity, count(*) AS count FROM metabolite
 UNION 
SELECT 'Marker' AS entity, count(*) AS count FROM marker
 UNION 
SELECT 'SNP' AS entity, count(*) AS count FROM sNP
 UNION 
SELECT 'Polymorphism' AS entity, count(*) AS count FROM polymorphism
 UNION 
SELECT 'Probe' AS entity, count(*) AS count FROM probe
 UNION 
SELECT 'Spot' AS entity, count(*) AS count FROM spot
 UNION 
SELECT 'ProbeSet' AS entity, count(*) AS count FROM probeSet
 UNION 
SELECT 'MassPeak' AS entity, count(*) AS count FROM massPeak
 UNION 
SELECT 'InvestigationFile' AS entity, count(*) AS count FROM investigationFile
 UNION 
SELECT 'Tissue' AS entity, count(*) AS count FROM tissue
 UNION 
SELECT 'SampleLabel' AS entity, count(*) AS count FROM sampleLabel
 UNION 
SELECT 'Sample' AS entity, count(*) AS count FROM sample
 UNION 
SELECT 'PairedSample' AS entity, count(*) AS count FROM pairedSample
 UNION 
SELECT 'Job' AS entity, count(*) AS count FROM job
 UNION 
SELECT 'Subjob' AS entity, count(*) AS count FROM subjob
 UNION 
SELECT 'Analysis' AS entity, count(*) AS count FROM analysis
 UNION 
SELECT 'ParameterSet' AS entity, count(*) AS count FROM parameterSet
 UNION 
SELECT 'ParameterName' AS entity, count(*) AS count FROM parameterName
 UNION 
SELECT 'ParameterValue' AS entity, count(*) AS count FROM parameterValue
 UNION 
SELECT 'DataSet' AS entity, count(*) AS count FROM dataSet
 UNION 
SELECT 'DataName' AS entity, count(*) AS count FROM dataName
 UNION 
SELECT 'DataValue' AS entity, count(*) AS count FROM dataValue
 UNION 
SELECT 'SelectedParameter' AS entity, count(*) AS count FROM selectedParameter
 UNION 
SELECT 'SelectedData' AS entity, count(*) AS count FROM selectedData
 UNION 
SELECT 'RScript' AS entity, count(*) AS count FROM rScript
 UNION 
SELECT 'HemoSample' AS entity, count(*) AS count FROM hemoSample
 UNION 
SELECT 'HemoGene' AS entity, count(*) AS count FROM hemoGene
 UNION 
SELECT 'HemoProbe' AS entity, count(*) AS count FROM hemoProbe
 UNION 
SELECT 'ObservedInference_derivedFrom' AS entity, count(*) AS count FROM observedInference_derivedFrom
 UNION 
SELECT 'Investigation_contacts' AS entity, count(*) AS count FROM investigation_contacts
 UNION 
SELECT 'ObservationElement_AlternateId' AS entity, count(*) AS count FROM observationElement_AlternateId
 UNION 
SELECT 'Measurement_categories' AS entity, count(*) AS count FROM measurement_categories
 UNION 
SELECT 'Panel_Individuals' AS entity, count(*) AS count FROM panel_Individuals
 UNION 
SELECT 'Panel_FounderPanels' AS entity, count(*) AS count FROM panel_FounderPanels
 UNION 
SELECT 'Protocol_Features' AS entity, count(*) AS count FROM protocol_Features
 UNION 
SELECT 'Protocol_subprotocols' AS entity, count(*) AS count FROM protocol_subprotocols
 UNION 
SELECT 'ProtocolApplication_Performer' AS entity, count(*) AS count FROM protocolApplication_Performer
 UNION 
SELECT 'WorkflowElement_PreviousSteps' AS entity, count(*) AS count FROM workflowElement_PreviousSteps
 UNION 
SELECT 'Marker_ReportsFor' AS entity, count(*) AS count FROM marker_ReportsFor
 UNION 
SELECT 'SNP_Polymorphism' AS entity, count(*) AS count FROM sNP_Polymorphism

;