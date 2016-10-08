/**
 * 
 */
package org.jkiss.dbeaver.ext.exasol.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.format.FormatStyle;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.exasol.editors.ExasolColumnDataTypeListProvider;
import org.jkiss.dbeaver.model.DBPDataKind;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPHiddenObject;
import org.jkiss.dbeaver.model.DBPNamedObject2;
import org.jkiss.dbeaver.model.impl.DBPositiveNumberTransformer;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTableColumn;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSDataType;
import org.jkiss.dbeaver.model.struct.DBSTypedObjectEx;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;

/**
 * @author Karl Griesser
 *
 */
public class ExasolTableColumn extends JDBCTableColumn<ExasolTableBase>
		implements DBSTableColumn, DBSTypedObjectEx, DBPHiddenObject, DBPNamedObject2 {

	private ExasolDataType dataType;
	private Boolean identity;
	private BigDecimal identityValue;
	private Integer keySeq;
	private String remarks;
	private Boolean isInDistKey;
	private String formatType;
	private Boolean changed = false;

	// -----------------
	// Constructors
	// -----------------

	public ExasolTableColumn(DBRProgressMonitor monitor, ExasolTableBase tableBase, ResultSet dbResult)
			throws DBException {
		super(tableBase, true);

		this.formatType = JDBCUtils.safeGetString(dbResult, "COLUMN_TYPE");
		setName(JDBCUtils.safeGetString(dbResult, "COLUMN_NAME"));
		setOrdinalPosition(JDBCUtils.safeGetInt(dbResult, "ORDINAL_POSITION"));
		setRequired(JDBCUtils.safeGetBoolean(dbResult, "COLUMN_IS_NULLABLE"));
		setDefaultValue(JDBCUtils.safeGetString(dbResult, "COLUMN_DEF"));
		setMaxLength(JDBCUtils.safeGetInt(dbResult, "COLUMN_SIZE"));
		setScale(JDBCUtils.safeGetInt(dbResult, "DECIMAL_DIGITS"));

		this.isInDistKey = JDBCUtils.safeGetBoolean(dbResult, "COLUMN_IS_DISTRIBUTION_KEY");
		this.identity = JDBCUtils.safeGetInteger(dbResult, "COLUMN_IDENTITY") == null ? false : true;
		if (identity)
			this.identityValue = JDBCUtils.safeGetBigDecimal(dbResult, "COLUMN_IDENTITY");
		this.remarks = JDBCUtils.safeGetString(dbResult, "COLUMN_COMMENT");
		this.dataType = tableBase.getDataSource().getDataType(monitor, JDBCUtils.safeGetString(dbResult, "TYPE_NAME"));
		this.keySeq = JDBCUtils.safeGetInteger(dbResult, "KEY_SEQ");
		this.changed = true;
		

	}

	public ExasolTableColumn(ExasolTableBase tableBase) {
		super(tableBase, false);

		setMaxLength(50L);
		setOrdinalPosition(-1);
		this.dataType = tableBase.getDataSource().getDataTypeCache().getCachedObject("VARCHAR");
		setTypeName(dataType.getFullyQualifiedName(DBPEvaluationContext.DML));
		setRequired(true);
	}

	// -----------------
	// Business Contract
	// -----------------

	@NotNull
	@Override
	public ExasolDataSource getDataSource() {
		return getTable().getDataSource();
	}

	@Override
	public DBPDataKind getDataKind() {
		return dataType.getDataKind();
	}

	@Override
	public String getTypeName() {
		return this.dataType.getName();
	}

	// -----------------
	// Properties
	// -----------------
	@Property(viewable = true, editable = false, order = 19)
	public ExasolTableBase getOwner() {
		return getTable();
	}

	@Nullable
	@Property(viewable = true, editable = true, updatable = true, order = 21, listProvider = ExasolColumnDataTypeListProvider.class) 
	public DBSDataType getDataType() {
		return dataType;
	}

	public void setDataType(ExasolDataType dataType) {
		if (! this.dataType.getTypeName().equals(dataType))
			this.changed = true;
		this.dataType = dataType;
	}

	@Override
	@Property(viewable = true,editable = true, updatable = true, order = 38)
	public long getMaxLength() {
		return super.getMaxLength();
	}

	public void setMaxLength(long maxLength) {
		if (this.maxLength != maxLength)
			this.changed = true;
		super.setMaxLength(maxLength);
	}

	@Override
	@Property(viewable = true, editable = true, updatable = true,valueRenderer = DBPositiveNumberTransformer.class, order = 39)
	public int getScale() {
		return super.getScale();
	}

	public void setScale(int scale) {
		if ( this.scale != scale)
			this.changed = true;
		super.setScale(scale);
	}

	@Property(viewable = true, editable = true, updatable =true, order = 46)
	@Nullable
	public BigDecimal getIdentityValue() {
		return this.identityValue;
	}

	public void setIdentityValue(BigDecimal identityValue) {
		this.identityValue = identityValue;
	}

	@Property(viewable = false, order = 40)
	public String getStringLength() {
		return "";
	}

	@Override
	@Property(viewable = false, editable = true, updatable = true, valueRenderer = DBPositiveNumberTransformer.class, order = 42)
	public int getPrecision() {
		return super.getPrecision();
	}

	public void setPrecision(int precision) {
		if (this.precision != precision)
			this.changed = true;
		super.precision = precision;
		this.precision = precision;

	}

	@Override
	@Property(viewable = true, order = 43, editable = true, updatable = true)
	public boolean isRequired() {
		return super.isRequired();
	}

	public void setRequired(boolean required) {
		super.setRequired(required);
	}

	@Override
	@Property(viewable = true, order = 44, editable = true, updatable = true)
	public String getDefaultValue() {
		return super.getDefaultValue();
	}

	public void setDefaultValue(String defaultValue) {
		super.setDefaultValue(defaultValue);
	}

	@Property(viewable = true,editable = true, updatable = true, order = 45)
	public Boolean isIdentity() {
		return identity;
	}

	public void setIdentity(Boolean identity) {
		this.identity = identity;
	}

	@Nullable
	@Override
	@Property(viewable = true, order = 999, editable = true, updatable = true)
	public String getDescription() {
		return remarks;
	}

	public void setDescription(String remarks) {
		this.remarks = remarks;
	}

	@Property(viewable = false, order = 120)
	public Integer getKeySeq() {
		return keySeq;
	}

	@Property(viewable = false,  order = 121)
	public Boolean isDistKey() {
		return isInDistKey;
	}


	// no hidden columns supported in exasol
	@Override
	public boolean isHidden() {
		return false;
	}

	public String getFormatType() {
		if (changed) {
			switch(this.dataType.getTypeName()) {
			case "VARCHAR":
			case "CHAR":
				this.scale = 0;
				return dataType.getTypeName() + "(" + Long.toString(this.maxLength) + ")";
			case "DECIMAL":
				return dataType.getTypeName() + "(" + Long.toString(this.maxLength) + "," + Long.toString(this.scale) + ")";
			default:
				return dataType.getTypeName();
			}
		}
		return this.formatType;
	}
	

}
