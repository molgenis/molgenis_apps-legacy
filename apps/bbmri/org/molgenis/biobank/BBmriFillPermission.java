package org.molgenis.biobank;

import org.molgenis.auth.MolgenisGroup;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisRole;
import org.molgenis.auth.MolgenisRoleGroupLink;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class BBmriFillPermission
{
	public static void fillPermission(Database db) throws DatabaseException
	{

		MolgenisRole mr = MolgenisRole.findByName(db, "AllUsers");
		// MolgenisRole mr = db.find(MolgenisRole.class, new QueryRule("name",
		// Operator.EQUALS, "AllUsers")).get(0);

		MolgenisEntity me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.Biobank");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp = new MolgenisPermission();
		mp.setEntity(me);
		mp.setRole_Id(mr.getId());
		mp.setPermission("read");
		db.add(mp);

		// app.ui.BiobankOverviewMenu
		me = MolgenisEntity.findByClassName(db, "app.ui.BiobankOverviewMenu");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp1 = new MolgenisPermission();
		mp1.setEntity(me);
		mp1.setRole_Id(mr.getId());
		mp1.setPermission("read");
		db.add(mp1);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.Biobank_Biodata");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp2 = new MolgenisPermission();
		mp2.setEntity(me);
		mp2.setRole_Id(mr.getId());
		mp2.setPermission("read");
		db.add(mp2);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.Biobank_Coordinator");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp3 = new MolgenisPermission();
		mp3.setEntity(me);
		mp3.setRole_Id(mr.getId());
		mp3.setPermission("read");
		db.add(mp3);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.BiobankDataType");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp4 = new MolgenisPermission();
		mp4.setEntity(me);
		mp4.setRole_Id(mr.getId());
		mp4.setPermission("read");
		db.add(mp4);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.Biobank_Topic");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp5 = new MolgenisPermission();
		mp5.setEntity(me);
		mp5.setRole_Id(mr.getId());
		mp5.setPermission("read");
		db.add(mp5);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.BiobankCategory");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp6 = new MolgenisPermission();
		mp6.setEntity(me);
		mp6.setRole_Id(mr.getId());
		mp6.setPermission("read");
		db.add(mp6);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.BiobankCoordinator");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp7 = new MolgenisPermission();
		mp7.setEntity(me);
		mp7.setRole_Id(mr.getId());
		mp7.setPermission("read");
		db.add(mp7);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.BiobankTopic");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp8 = new MolgenisPermission();
		mp8.setEntity(me);
		mp8.setRole_Id(mr.getId());
		mp8.setPermission("read");
		db.add(mp8);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.ChangeLog");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp9 = new MolgenisPermission();
		mp9.setEntity(me);
		mp9.setRole_Id(mr.getId());
		mp9.setPermission("read");
		db.add(mp9);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.bbmri.Welcome");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp10 = new MolgenisPermission();
		mp10.setEntity(me);
		mp10.setRole_Id(mr.getId());
		mp10.setPermission("read");
		db.add(mp10);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.auth.Institute");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp11 = new MolgenisPermission();
		mp11.setEntity(me);
		mp11.setRole_Id(mr.getId());
		mp11.setPermission("read");
		db.add(mp11);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.core.Ontology");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp12 = new MolgenisPermission();
		mp12.setEntity(me);
		mp12.setRole_Id(mr.getId());
		mp12.setPermission("read");
		db.add(mp12);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.core.OntologyTerm");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp13 = new MolgenisPermission();
		mp13.setEntity(me);
		mp13.setRole_Id(mr.getId());
		mp13.setPermission("read");
		db.add(mp13);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.organization.Investigation");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp14 = new MolgenisPermission();
		mp14.setEntity(me);
		mp14.setRole_Id(mr.getId());
		mp14.setPermission("read");
		db.add(mp14);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.Species");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp15 = new MolgenisPermission();
		mp15.setEntity(me);
		mp15.setRole_Id(mr.getId());
		mp15.setPermission("read");
		db.add(mp15);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.ObservationElement");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp16 = new MolgenisPermission();
		mp16.setEntity(me);
		mp16.setRole_Id(mr.getId());
		mp16.setPermission("read");
		db.add(mp16);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.ObservableFeature");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp17 = new MolgenisPermission();
		mp17.setEntity(me);
		mp17.setRole_Id(mr.getId());
		mp17.setPermission("read");
		db.add(mp17);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.ObservationTarget");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp18 = new MolgenisPermission();
		mp18.setEntity(me);
		mp18.setRole_Id(mr.getId());
		mp18.setPermission("read");
		db.add(mp18);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.Measurement");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp19 = new MolgenisPermission();
		mp19.setEntity(me);
		mp19.setRole_Id(mr.getId());
		mp19.setPermission("read");
		db.add(mp19);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.Individual");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp20 = new MolgenisPermission();
		mp20.setEntity(me);
		mp20.setRole_Id(mr.getId());
		mp20.setPermission("read");
		db.add(mp20);

		me = MolgenisEntity.findByClassName(db, "org.molgenis.pheno.ObservedValue");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp21 = new MolgenisPermission();
		mp21.setEntity(me);
		mp21.setRole_Id(mr.getId());
		mp21.setPermission("read");
		db.add(mp21);

		me = MolgenisEntity.findByClassName(db, "app.ui.CohortsFormController");
		System.out.println("molgenisRole:" + mr.getId() + "molgenisEntity" + me.getId());
		MolgenisPermission mp22 = new MolgenisPermission();
		mp22.setEntity(me);
		mp22.setRole_Id(mr.getId());
		mp22.setPermission("read");
		db.add(mp22);

		// remove the anonymous user from AllUsers
		MolgenisRoleGroupLink mrgl = new MolgenisRoleGroupLink();
		int mgId = MolgenisGroup.findByName(db, "AllUsers").getId();
		System.out.println(mgId);

		int mrId = MolgenisRole.findByName(db, "anonymous").getId();
		System.out.println(mrId);

		MolgenisRoleGroupLink a = MolgenisRoleGroupLink.findByGroupRole(db, mgId, mrId);

		db.remove(a);
	}
}