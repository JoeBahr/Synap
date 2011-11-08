/**
 * This class describe the content of an NTP message
 * and provides basic instantiation and services
 */
package com.leopal.synap;


import java.util.Arrays;

/**
 * A Simple Network Time Protocol (SNTP) message.
 * 
 * Time are defined as "long" for the number of milliseconds since Jan. 1, 1970, midnight GMT. 
 * 
 * Based on NTP message define by package net.sf.atomicdate.sntp
 */
public final class clSyncNTPMessage implements java.io.Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Class attributes. **************************************************************************
	
	/** Leap Indicator: no warning. */
	public static final byte LI_NO_WARN=0x00;
	/** Leap Indicator: last minute has 61 seconds. */
	public static final byte LI_61_SECS=0x01;
	/** Leap Indicator: last minute has 59 seconds. */
	public static final byte LI_59_SECS=0x01;
	/** Leap Indicator: alarm condition. */
	public static final byte LI_ALARM=0x02;
	
	/** Version Number: v1 */
	public static final byte VN_1=0x01;
	/** Version Number: v2 */
	public static final byte VN_2=0x02;
	/** Version Number: v3 */
	public static final byte VN_3=0x03;
	/** Version Number: v4 */
	public static final byte VN_4=0x04;
	
	/** Mode: reserved. */
	public static final byte MODE_RESERVED=0x00;
	/** Mode: symmetric active. */
	public static final byte MODE_SYM_ACTIVE=0x01;
	/** Mode: symmetric passive. */
	public static final byte MODE_SYM_PASSIVE=0x02;
	/** Mode: client. */
	public static final byte MODE_CLIENT=0x03;
	/** Mode: server. */
	public static final byte MODE_SERVER=0x04;
	/** Mode: broadcast. */
	public static final byte MODE_BROADCAST=0x05;
	/** Mode: reserved for NTP control message. */
	public static final byte MODE_RESERVED_NTP=0x06;
	/** Mode: reserved for private use. */
	public static final byte MODE_RESERVED_PRIVATE=0x07;
	
	/** Stratum: unspecified. */
	public static final byte STRATUM_UNSPECIFIED=0x00;
	/** Stratum: primary reference. */
	public static final byte STRATUM_PRIMARY=0x01;
	
	/** Maximum message length (in bytes). */
	public static final int MAXIMUM_LENGTH=384;	// without authentication.
	
	// Default values:
	private static final byte DEFAULT_POLL_INTERVAL=0;
	private static final byte DEFAULT_PRECISION=0;
	private static final double DEFAULT_ROOT_DELAY=0.0F;
	private static final double DEFAULT_ROOT_DISPERSION=0.0F;
	private static final byte[] DEFAULT_REFERENCE_IDENTIFIER="LOCL".getBytes();
	private static final long DEFAULT_REFERENCE_DATE=0;
	private static final long DEFAULT_ORIGINATE_DATE=0;
	private static final long DEFAULT_RECEIVE_DATE=0;
	private static final long DEFAULT_TRANSMIT_DATE=0;
	
	
	// Instance attributes. ***********************************************************************
	
	/** Leap Indicator. */
	private byte byLeapIndicator;
	
	/** Version Number. */
	private byte byVersionNumber;
	
	/** Mode. */
	private byte byMode;
	
	/** Stratum. */
	private byte byStratum;
	
	/** Poll Interval. */
	private byte byPollInterval;
	
	/** Precision. */
	private byte byPrecision;
	
	/** Rood Delay. */
	private double dRootDelay;
	
	/** Root Dispersion. */
	private double dRootDispersion;
	
	/** Reference Identifier. */
	private byte[] sReferenceIdentifier;
	
	/** Reference Date. */
	private long tReferenceDate;
	
	/** Originate Date. */
	private long tOriginateDate;
	
	/** Receive Date. */
	private long tReceiveDate;
	
	/** Transmit Date. */
	private long tTransmitDate;
	
	
	// Instance methods. **************************************************************************
	
	/**
	 * Default constructor. Builds an SNTP message ready for client mode operation.
	 */
	public clSyncNTPMessage()
	{
		// Values default to client mode:
		setLeapIndicator(LI_NO_WARN);
		setVersionNumber(VN_4);
		setMode(MODE_CLIENT);
		setStratum(STRATUM_UNSPECIFIED);
		setPollInterval(DEFAULT_POLL_INTERVAL);
		setPrecision(DEFAULT_PRECISION);
		setRootDelay(DEFAULT_ROOT_DELAY);
		setRootDispersion(DEFAULT_ROOT_DISPERSION);
		setReferenceIdentifier(DEFAULT_REFERENCE_IDENTIFIER);
		setReferenceDate(DEFAULT_REFERENCE_DATE);
		setOriginateDate(DEFAULT_ORIGINATE_DATE);
		setReferenceDate(DEFAULT_RECEIVE_DATE);
		setTransmitDate(DEFAULT_TRANSMIT_DATE);
	}
	
	
	/**
	 * Returns the Leap Indicator.
	 * 
	 * @return the Leap Indicator.
	 */
	public byte getLeapIndicator()
	{
		return byLeapIndicator;
	}
	
	
	/**
	 * Sets the Leap Indicator.
	 * 
	 * @param byLeapIndicator the Leap Indicator.
	 */
	public void setLeapIndicator(final byte byLeapIndicator)
	{
		this.byLeapIndicator=byLeapIndicator;
	}
	
	
	/**
	 * Returns the Version Number.
	 * 
	 * @return the Version Number.
	 */
	public byte getVersionNumber()
	{
		return byVersionNumber;
	}
	
	
	/**
	 * Sets the Version Number.
	 * 
	 * @param byVersionNumber the Version Number.
	 */
	public void setVersionNumber(final byte byVersionNumber)
	{
		this.byVersionNumber=byVersionNumber;
	}
	
	
	/**
	 * Returns the Mode.
	 * 
	 * @return the Mode.
	 */
	public byte getMode()
	{
		return byMode;
	}
	
	
	/**
	 * Sets the Mode.
	 * 
	 * @param byMode the Mode.
	 */
	public void setMode(final byte byMode)
	{
		this.byMode=byMode;
	}
	
	
	/**
	 * Returns the Stratum.
	 * 
	 * @return the Stratum.
	 */
	public byte getStratum()
	{
		return byStratum;
	}
	
	
	/**
	 * Sets the Stratum.
	 * 
	 * @param byStratum the Stratum.
	 */
	public void setStratum(final byte byStratum)
	{
		this.byStratum=byStratum;
	}
	
	
	/**
	 * Returns the Poll Interval.
	 * 
	 * @return the Poll Interval.
	 */
	public byte getPollInterval()
	{
		return byPollInterval;
	}
	
	
	/**
	 * Sets the Poll Interval.
	 * 
	 * @param byPollInterval the Poll Interval.
	 */
	public void setPollInterval(final byte byPollInterval)
	{
		this.byPollInterval=byPollInterval;
	}
	
	
	/**
	 * Returns the Precision.
	 * 
	 * @return the Precision.
	 */
	public byte getPrecision()
	{
		return byPrecision;
	}
	
	
	/**
	 * Sets the Precision.
	 * 
	 * @param byPrecision the Precision.
	 */
	public void setPrecision(final byte byPrecision)
	{
		this.byPrecision=byPrecision;
	}
	
	
	/**
	 * Returns the Root Delay.
	 * 
	 * @return the Root Delay.
	 */
	public double getRootDelay()
	{
		return dRootDelay;
	}
	
	
	/**
	 * Sets the Root Delay.
	 * 
	 * @param dRootDelay the Root Delay.
	 */
	public void setRootDelay(final double dRootDelay)
	{
		this.dRootDelay=dRootDelay;
	}
	
	
	/**
	 * Returns the Root Dispersion.
	 * 
	 * @return the Root Dispersion.
	 */
	public double getRootDispersion()
	{
		return dRootDispersion;
	}
	
	
	/**
	 * Sets the Root Dispersion.
	 * 
	 * @param dRootDispersion the Root Dispersion.
	 */
	public void setRootDispersion(final double dRootDispersion)
	{
		this.dRootDispersion=dRootDispersion;
	}
	
	
	/**
	 * Returns the Reference Identifier.
	 * 
	 * @return the Reference Identifier.
	 */
	public byte[] getReferenceIdentifier()
	{
		return sReferenceIdentifier;
	}
	
	
	/**
	 * Sets the Reference Identifier.
	 * 
	 * @param sReferenceIdentifier the Reference Identifier.
	 */
	public void setReferenceIdentifier(final byte[] sReferenceIdentifier)
	{
		this.sReferenceIdentifier=sReferenceIdentifier;
	}
	
	
	/**
	 * Returns the Reference Date.
	 * 
	 * @return the Reference Date.
	 */
	public long getReferenceDate()
	{
		return tReferenceDate;
	}
	
	
	/**
	 * Sets the Reference Date.
	 * 
	 * @param defaultReferenceDate the Reference Date.
	 */
	public void setReferenceDate(final long defaultReferenceDate)
	{
		this.tReferenceDate=defaultReferenceDate;
	}
	
	
	/**
	 * Returns the Originate Date.
	 * 
	 * @return the Originate Date.
	 */
	public long getOriginateDate()
	{
		return tOriginateDate;
	}
	
	
	/**
	 * Sets the Originate Date.
	 * 
	 * @param defaultOriginateDate the Originate Date.
	 */
	public void setOriginateDate(final long defaultOriginateDate)
	{
		this.tOriginateDate=defaultOriginateDate;
	}
	
	
	/**
	 * Returns the Receive Date.
	 * 
	 * @return the Receive Date.
	 */
	public long getReceiveDate()
	{
		return tReceiveDate;
	}
	
	
	/**
	 * Sets the Receive Date.
	 * 
	 * @param tReceiveDate the Receive Date.
	 */
	public void setReceiveDate(final long tReceiveDate)
	{
		this.tReceiveDate=tReceiveDate;
	}
	
	
	/**
	 * Returns the Transmit Date.
	 * 
	 * @return the Transmit Date.
	 */
	public long getTransmitDate()
	{
		return tTransmitDate;
	}
	
	
	/**
	 * Sets the Transmit Date.
	 * 
	 * @param defaultTransmitDate the Transmit Date.
	 */
	public void setTransmitDate(final long defaultTransmitDate)
	{
		this.tTransmitDate=defaultTransmitDate;
	}
	
	
	// See Object for details.
	public boolean equals(final Object obj)
	{
		boolean equals=false;
		if (obj==this) {
			equals=true;
		} else {
			if (obj!=null && (obj instanceof clSyncNTPMessage)) {
				final clSyncNTPMessage other=(clSyncNTPMessage)obj;
				equals=(other.byLeapIndicator==this.byLeapIndicator)
					&& (other.byVersionNumber==this.byVersionNumber)
					&& (other.byMode==this.byMode)
					&& (other.byStratum==this.byStratum)
					&& (other.byPollInterval==this.byPollInterval)
					&& (other.byPrecision==this.byPrecision)
					&& (other.dRootDelay==this.dRootDelay)
					&& (other.dRootDispersion==this.dRootDispersion)
					&& Arrays.equals(other.sReferenceIdentifier, this.sReferenceIdentifier)
					&& (other.tReferenceDate==this.tReferenceDate)
					&& (other.tOriginateDate==this.tOriginateDate)
					&& (other.tReceiveDate==this.tReceiveDate)
					&& (other.tTransmitDate==this.tTransmitDate);
			}
		}
		
		return equals;
	}
	
	
	// See Object for details.
	public String toString()
	{
		final StringBuffer sb=new StringBuffer();
		sb.append("LeapIndicator=").append(byLeapIndicator).append(", ");
		sb.append("VersionNumber=").append(byVersionNumber).append(", ");
		sb.append("Mode=").append(byMode).append(", ");
		sb.append("Stratum=").append(byStratum).append(", ");
		sb.append("PollInterval=").append(byPollInterval).append(", ");
		sb.append("Precision=").append(byPrecision).append(", ");
		sb.append("RootDelay=").append(dRootDelay).append(", ");
		sb.append("RootDispersion=").append(dRootDispersion).append(", ");
		sb.append("ReferenceIdentifier=").append(new String(sReferenceIdentifier)).append(", ");
		sb.append("ReferenceDate=").append(tReferenceDate).append(", ");
		sb.append("OriginateDate=").append(tOriginateDate).append(", ");
		sb.append("ReceiveTimetamp=").append(tReceiveDate).append(", ");
		sb.append("TransmitDate=").append(tTransmitDate);
		
		return sb.toString();
	}
	
	
}
