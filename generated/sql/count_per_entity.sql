SELECT 'MolgenisRole' AS entity, count(*) AS count FROM MolgenisRole WHERE __Type = 'MolgenisRole'
 UNION 
SELECT 'MolgenisGroup' AS entity, count(*) AS count FROM MolgenisRole NATURAL JOIN MolgenisGroup WHERE __Type = 'MolgenisGroup'
 UNION 
SELECT 'MolgenisRoleGroupLink' AS entity, count(*) AS count FROM MolgenisRoleGroupLink
 UNION 
SELECT 'Person' AS entity, count(*) AS count FROM MolgenisRole NATURAL JOIN Person WHERE __Type = 'Person'
 UNION 
SELECT 'Institute' AS entity, count(*) AS count FROM Institute
 UNION 
SELECT 'MolgenisUser' AS entity, count(*) AS count FROM MolgenisRole NATURAL JOIN Person NATURAL JOIN MolgenisUser WHERE __Type = 'MolgenisUser'
 UNION 
SELECT 'MolgenisPermission' AS entity, count(*) AS count FROM MolgenisPermission
 UNION 
SELECT 'OntologyTerm' AS entity, count(*) AS count FROM OntologyTerm WHERE __Type = 'OntologyTerm'
 UNION 
SELECT 'Ontology' AS entity, count(*) AS count FROM Ontology
 UNION 
SELECT 'MolgenisFile' AS entity, count(*) AS count FROM MolgenisFile WHERE __Type = 'MolgenisFile'
 UNION 
SELECT 'RuntimeProperty' AS entity, count(*) AS count FROM RuntimeProperty
 UNION 
SELECT 'Publication' AS entity, count(*) AS count FROM Publication
 UNION 
SELECT 'UseCase' AS entity, count(*) AS count FROM UseCase
 UNION 
SELECT 'MolgenisEntity' AS entity, count(*) AS count FROM MolgenisEntity
 UNION 
SELECT 'ObservedInference' AS entity, count(*) AS count FROM ObservedValue NATURAL JOIN ObservedInference WHERE __Type = 'ObservedInference'
 UNION 
SELECT 'DataFile' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN DataFile WHERE __Type = 'DataFile'
 UNION 
SELECT 'Data' AS entity, count(*) AS count FROM ProtocolApplication NATURAL JOIN Data WHERE __Type = 'Data'
 UNION 
SELECT 'BinaryDataMatrix' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN BinaryDataMatrix WHERE __Type = 'BinaryDataMatrix'
 UNION 
SELECT 'CSVDataMatrix' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN CSVDataMatrix WHERE __Type = 'CSVDataMatrix'
 UNION 
SELECT 'DecimalDataElement' AS entity, count(*) AS count FROM DecimalDataElement
 UNION 
SELECT 'TextDataElement' AS entity, count(*) AS count FROM TextDataElement
 UNION 
SELECT 'OriginalFile' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN OriginalFile WHERE __Type = 'OriginalFile'
 UNION 
SELECT 'Investigation' AS entity, count(*) AS count FROM Investigation
 UNION 
SELECT 'Species' AS entity, count(*) AS count FROM OntologyTerm NATURAL JOIN Species WHERE __Type = 'Species'
 UNION 
SELECT 'AlternateId' AS entity, count(*) AS count FROM OntologyTerm NATURAL JOIN AlternateId WHERE __Type = 'AlternateId'
 UNION 
SELECT 'ObservationElement' AS entity, count(*) AS count FROM ObservationElement WHERE __Type = 'ObservationElement'
 UNION 
SELECT 'ObservationTarget' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget WHERE __Type = 'ObservationTarget'
 UNION 
SELECT 'ObservableFeature' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature WHERE __Type = 'ObservableFeature'
 UNION 
SELECT 'Measurement' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Measurement WHERE __Type = 'Measurement'
 UNION 
SELECT 'Category' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN Category WHERE __Type = 'Category'
 UNION 
SELECT 'Individual' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN Individual WHERE __Type = 'Individual'
 UNION 
SELECT 'Location' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN Location WHERE __Type = 'Location'
 UNION 
SELECT 'Panel' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN Panel WHERE __Type = 'Panel'
 UNION 
SELECT 'ObservedValue' AS entity, count(*) AS count FROM ObservedValue WHERE __Type = 'ObservedValue'
 UNION 
SELECT 'Protocol' AS entity, count(*) AS count FROM Protocol WHERE __Type = 'Protocol'
 UNION 
SELECT 'ProtocolApplication' AS entity, count(*) AS count FROM ProtocolApplication WHERE __Type = 'ProtocolApplication'
 UNION 
SELECT 'ProtocolDocument' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN ProtocolDocument WHERE __Type = 'ProtocolDocument'
 UNION 
SELECT 'Workflow' AS entity, count(*) AS count FROM Protocol NATURAL JOIN Workflow WHERE __Type = 'Workflow'
 UNION 
SELECT 'WorkflowElement' AS entity, count(*) AS count FROM WorkflowElement
 UNION 
SELECT 'WorkflowElementParameter' AS entity, count(*) AS count FROM WorkflowElementParameter
 UNION 
SELECT 'Chromosome' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Chromosome WHERE __Type = 'Chromosome'
 UNION 
SELECT 'NMRBin' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN NMRBin WHERE __Type = 'NMRBin'
 UNION 
SELECT 'Clone' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Clone WHERE __Type = 'Clone'
 UNION 
SELECT 'DerivedTrait' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN DerivedTrait WHERE __Type = 'DerivedTrait'
 UNION 
SELECT 'EnvironmentalFactor' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN EnvironmentalFactor WHERE __Type = 'EnvironmentalFactor'
 UNION 
SELECT 'Gene' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Gene WHERE __Type = 'Gene'
 UNION 
SELECT 'Transcript' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Transcript WHERE __Type = 'Transcript'
 UNION 
SELECT 'Protein' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Protein WHERE __Type = 'Protein'
 UNION 
SELECT 'Metabolite' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Metabolite WHERE __Type = 'Metabolite'
 UNION 
SELECT 'Marker' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Marker WHERE __Type = 'Marker'
 UNION 
SELECT 'SNP' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Marker NATURAL JOIN SNP WHERE __Type = 'SNP'
 UNION 
SELECT 'Polymorphism' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Polymorphism WHERE __Type = 'Polymorphism'
 UNION 
SELECT 'Probe' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Probe WHERE __Type = 'Probe'
 UNION 
SELECT 'Spot' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN Probe NATURAL JOIN Spot WHERE __Type = 'Spot'
 UNION 
SELECT 'ProbeSet' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN ProbeSet WHERE __Type = 'ProbeSet'
 UNION 
SELECT 'MassPeak' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN MassPeak WHERE __Type = 'MassPeak'
 UNION 
SELECT 'InvestigationFile' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN InvestigationFile WHERE __Type = 'InvestigationFile'
 UNION 
SELECT 'Tissue' AS entity, count(*) AS count FROM OntologyTerm NATURAL JOIN Tissue WHERE __Type = 'Tissue'
 UNION 
SELECT 'SampleLabel' AS entity, count(*) AS count FROM OntologyTerm NATURAL JOIN SampleLabel WHERE __Type = 'SampleLabel'
 UNION 
SELECT 'Sample' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN Sample WHERE __Type = 'Sample'
 UNION 
SELECT 'PairedSample' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN PairedSample WHERE __Type = 'PairedSample'
 UNION 
SELECT 'Job' AS entity, count(*) AS count FROM Job
 UNION 
SELECT 'Subjob' AS entity, count(*) AS count FROM Subjob
 UNION 
SELECT 'Analysis' AS entity, count(*) AS count FROM Analysis
 UNION 
SELECT 'ParameterSet' AS entity, count(*) AS count FROM ParameterSet
 UNION 
SELECT 'ParameterName' AS entity, count(*) AS count FROM ParameterName
 UNION 
SELECT 'ParameterValue' AS entity, count(*) AS count FROM ParameterValue
 UNION 
SELECT 'DataSet' AS entity, count(*) AS count FROM DataSet
 UNION 
SELECT 'DataName' AS entity, count(*) AS count FROM DataName
 UNION 
SELECT 'DataValue' AS entity, count(*) AS count FROM DataValue
 UNION 
SELECT 'SelectedParameter' AS entity, count(*) AS count FROM SelectedParameter
 UNION 
SELECT 'SelectedData' AS entity, count(*) AS count FROM SelectedData
 UNION 
SELECT 'RScript' AS entity, count(*) AS count FROM MolgenisFile NATURAL JOIN InvestigationFile NATURAL JOIN RScript WHERE __Type = 'RScript'
 UNION 
SELECT 'HemoSample' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservationTarget NATURAL JOIN HemoSample WHERE __Type = 'HemoSample'
 UNION 
SELECT 'HemoGene' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN HemoGene WHERE __Type = 'HemoGene'
 UNION 
SELECT 'HemoProbe' AS entity, count(*) AS count FROM ObservationElement NATURAL JOIN ObservableFeature NATURAL JOIN HemoProbe WHERE __Type = 'HemoProbe'
 UNION 
SELECT 'HemoSampleGroup' AS entity, count(*) AS count FROM HemoSampleGroup
 UNION 
SELECT 'ObservedInference_derivedFrom' AS entity, count(*) AS count FROM ObservedInference_derivedFrom
 UNION 
SELECT 'Investigation_contacts' AS entity, count(*) AS count FROM Investigation_contacts
 UNION 
SELECT 'ObservationElement_AlternateId' AS entity, count(*) AS count FROM ObservationElement_AlternateId
 UNION 
SELECT 'Measurement_categories' AS entity, count(*) AS count FROM Measurement_categories
 UNION 
SELECT 'Panel_Individuals' AS entity, count(*) AS count FROM Panel_Individuals
 UNION 
SELECT 'Panel_FounderPanels' AS entity, count(*) AS count FROM Panel_FounderPanels
 UNION 
SELECT 'Protocol_Features' AS entity, count(*) AS count FROM Protocol_Features
 UNION 
SELECT 'Protocol_subprotocols' AS entity, count(*) AS count FROM Protocol_subprotocols
 UNION 
SELECT 'ProtocolApplication_Performer' AS entity, count(*) AS count FROM ProtocolApplication_Performer
 UNION 
SELECT 'WorkflowElement_PreviousSteps' AS entity, count(*) AS count FROM WorkflowElement_PreviousSteps
 UNION 
SELECT 'Marker_ReportsFor' AS entity, count(*) AS count FROM Marker_ReportsFor
 UNION 
SELECT 'SNP_Polymorphism' AS entity, count(*) AS count FROM SNP_Polymorphism

;